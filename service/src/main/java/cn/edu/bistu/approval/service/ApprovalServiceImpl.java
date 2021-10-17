package cn.edu.bistu.approval.service;

import cn.edu.bistu.approval.WorkOrderFinisher;
import cn.edu.bistu.approval.WorkOrderFinisherFactory;
import cn.edu.bistu.approval.mapper.ApprovalRecordMapper;
import cn.edu.bistu.common.BeanUtils;
import cn.edu.bistu.common.exception.ResultCodeException;
import cn.edu.bistu.constants.ApprovalOperation;
import cn.edu.bistu.constants.ResultCodeEnum;
import cn.edu.bistu.constants.WorkOrderStatus;
import cn.edu.bistu.flow.dao.FlowDaoImpl;
import cn.edu.bistu.model.common.result.DaoResult;
import cn.edu.bistu.model.common.result.ServiceResult;
import cn.edu.bistu.model.common.result.ServiceResultImpl;
import cn.edu.bistu.model.entity.ApprovalRecord;
import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.model.entity.WorkOrderHistory;
import cn.edu.bistu.model.vo.WorkOrderVo;
import cn.edu.bistu.workOrder.dao.WorkOrderDao;
import cn.edu.bistu.workOrder.dao.WorkOrderDaoImpl;
import cn.edu.bistu.workOrder.dao.WorkOrderHistoryDao;
import cn.edu.bistu.workOrder.service.WorkOrderHistoryService;
import cn.edu.bistu.wx.service.WxMiniApi;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
public class ApprovalServiceImpl implements ApprovalService {

    @Autowired
    FlowDaoImpl flowDao;

    @Qualifier("workOrderDaoImpl")
    @Autowired
    WorkOrderDao workOrderDao;

    @Autowired
    ApprovalRecordMapper approvalRecordMapper;

    @Autowired
    WorkOrderHistoryService workOrderHistoryService;

    @Autowired
    WxMiniApi wxMiniApi;

    @Autowired
    WorkOrderHistoryDao workOrderHistoryDao;

    @Autowired
    WorkOrderFinisherFactory workOrderFinisherFactory;


    /**
     * 工单审批通过逻辑，若工单处于最后一个节点，触发工单结束逻辑；否则，触发工单流转逻辑。
     *
     * @param approvalRecord 审批记录
     */
    @Transactional
    @Override
    public void pass(ApprovalRecord approvalRecord) {
        Long workOrderId = approvalRecord.getWorkOrderId();

        DaoResult<WorkOrderVo> daoWorkOrder = workOrderDao.getOneWorkOrderById(workOrderId);
        WorkOrderVo workOrderVo = daoWorkOrder.getResult();

        if(workOrderVo==null) {
            throw new ResultCodeException("workOrder id: " + workOrderId, ResultCodeEnum.WORKORDER_NOT_EXISTS);
        }

        //检查用户是否有权限审批该工单
        checkApprovalRightOfUser(workOrderVo, approvalRecord.getApproverId());

        //检查工单是否已经结束
        checkIfWorkOrderHasFinished(workOrderVo);

        //查看工单下一个审批节点，没有下一个节点时返回null
        Long nextFlowNodeId = workOrderVo.getFlowNode().getNextId();

        //工单处于最后一个审批节点，工单结束
        if (nextFlowNodeId==null) {
            workOrderFinish(workOrderFinisherFactory.getFinisher("approvalType"), workOrderVo, approvalRecord, WorkOrderStatus.COMPLETED_SUCCESSFULLY, ApprovalOperation.PASS);
        }
        //工单流转
        else {
            WorkOrderFlowToNext(workOrderVo, approvalRecord, nextFlowNodeId);
        }
    }

    /**
     * 工单流转逻辑，将工单流转到下一个节点，并保存审批记录，该方法的触发条件是审批通过或提交工单。
     *
     * @param workOrder      待流转工单，待完善信息：工单状态，是否被审批过，工单下一个审批节点。
     * @param approvalRecord 造成工单结束的审批记录，待完善信息：审批操作，审批节点id，审批时间。
     * @param nextFlowNodeId 下一个审批节点id
     */
    private void WorkOrderFlowToNext(WorkOrder workOrder, ApprovalRecord approvalRecord, Long nextFlowNodeId) {

        //保存审批记录
        prepareApprovalRecord(approvalRecord, workOrder.getFlowNodeId(), ApprovalOperation.PASS);
        approvalRecordMapper.insert(approvalRecord);

        //更新工单
        workOrder.setStatus(0);     //在审
        workOrder.setIsExamined(1);  //已经被审批过
        workOrder.setFlowNodeId(nextFlowNodeId);    //更新工单审批节点
        ((WorkOrderDaoImpl)workOrderDao).getWorkOrderMapper().updateById(workOrder);
    }


    @Override
    public ServiceResult<Page<WorkOrderVo>> listWorkOrderToBeApproved(Long visitorId, Page<WorkOrderVo> page, WorkOrderVo workOrderVo) {
        DaoResult<Page<WorkOrderVo>> pageData = workOrderDao.getApprovalWorkOrderPage(page, visitorId, workOrderVo);
        return new ServiceResultImpl<>(pageData.getResult());
    }

    @Transactional
    @Override
    public void reject(ApprovalRecord approvalRecord){
        Long workOrderId = approvalRecord.getWorkOrderId();

        DaoResult<WorkOrderVo> daoWorkOrder = workOrderDao.getOneWorkOrderById(workOrderId);
        WorkOrderVo workOrderVo = daoWorkOrder.getResult();

        if(workOrderVo==null) {
            throw new ResultCodeException("workOrder id: " + workOrderId, ResultCodeEnum.WORKORDER_NOT_EXISTS);
        }

        //检查用户是否有权限审批该工单
        checkApprovalRightOfUser(workOrderVo, approvalRecord.getApproverId());

        //检查工单是否已经结束
        checkIfWorkOrderHasFinished(workOrderVo);

        workOrderFinish(workOrderFinisherFactory.getFinisher("approvalType"), workOrderVo, approvalRecord, WorkOrderStatus.NOT_APPROVED, ApprovalOperation.REJECT);

    }


    /**
     * 检查接口来访者是否是工单所在节点的审批者
     * @param userId 来访者id
     * @param workOrderVo 待审批工单
     */
    private void checkApprovalRightOfUser(WorkOrderVo workOrderVo,  Long userId) {

        if (!workOrderVo.getFlowNode().getApproverId().equals(userId)) {
            throw new ResultCodeException("user id:" + userId,
                    ResultCodeEnum.HAVE_NO_RIGHT);
        }
    }

    /**
     * 判断工单是否已经结束
     *
     * @param workOrderVo 待查询的工单
     * @throws ResultCodeException 如果工单结束，抛出该异常
     */
    private void checkIfWorkOrderHasFinished(WorkOrderVo workOrderVo) {


        //若工单已结束，审批操作非法
        if (workOrderVo.getIsFinished().equals(1)) {
            throw new ResultCodeException(workOrderVo,
                    ResultCodeEnum.WORKORDER_BEEN_FINISHED);
        }

    }

    /**
     * 生成对应工单的历史记录
     * @param workOrder 工单
     */
    public void generateWorkOrderHistory(cn.edu.bistu.model.entity.WorkOrderStatus beforeStatus, WorkOrder workOrder) {
        //生成历史工单
        WorkOrderHistory workOrderHistory = new WorkOrderHistory();
        BeanUtils.copyProperties(workOrder, workOrderHistory);
        workOrderHistory.setWorkOrderId(workOrder.getId());
        workOrderHistory.setBeforeFinishedStatus(beforeStatus.getValue());
        workOrderHistory.setBeforeFinishedFlowNodeId(workOrder.getFlowNodeId());
        workOrderHistoryDao.insertOne(workOrderHistory);
    }

    /**
     * 工单结束逻辑:如果是因为审批结束的，需要保存审批记录，更新工单状态并保存，生成历史工单并保存，将工单结束状态通过微信发送给工单发起者。
     * @param workOrderFinisher 工单结束的任务委托给对应的Finisher即可
     * @param workOrder      待结束的工单，待完善信息：工单状态，工单是否结束，工单是否被审批。
     * @param approvalRecord 造成工单结束的审批记录，待完善信息：审批操作，审批节点id，审批时间。如果不是因为审批结束的工单，传入null
     * @param finishStatus 工单结束的原因（REJECT或PASS或revoke或invalidation），以及工单结束后的状态
     * @param approvalOperation 如果工单结束是因为审批操作，那么传入对应的审批操作枚举值，否则传入null即可
     */
    @Transactional
    public void workOrderFinish(WorkOrderFinisher workOrderFinisher, WorkOrder workOrder, ApprovalRecord approvalRecord, WorkOrderStatus finishStatus,
                                ApprovalOperation approvalOperation) {

        workOrderFinisher.finishWorkOrder(workOrder, approvalRecord, finishStatus, approvalOperation);

    }

    public void prepareApprovalRecord(ApprovalRecord approvalRecord, Long flowNodeId, ApprovalOperation approvalOperation) {
        approvalRecord.setFlowNodeId(flowNodeId);
        approvalRecord.setOperation(approvalOperation.getCode());
    }
}
