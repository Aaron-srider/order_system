package cn.edu.bistu.approval.service;

import cn.edu.bistu.approval.WorkOrderFinishWrapper;
import cn.edu.bistu.approval.WorkOrderFinisher;
import cn.edu.bistu.approval.WorkOrderFinisherFactory;
import cn.edu.bistu.approval.WorkOrderFlower;
import cn.edu.bistu.approval.mapper.ApprovalRecordMapper;
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
import cn.edu.bistu.model.vo.WorkOrderVo;
import cn.edu.bistu.workOrder.dao.WorkOrderDao;
import cn.edu.bistu.workOrder.dao.WorkOrderHistoryDao;
import cn.edu.bistu.workOrder.service.ActualApproverFinalizer;
import cn.edu.bistu.workOrder.service.WorkOrderHistoryService;
import cn.edu.bistu.wx.service.WxMiniApi;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author wc
 */
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

    @Autowired
    ActualApproverFinalizer actualApproverFinalizer;

    @Autowired
    WorkOrderFlower workOrderFlower;

    /**
     * 工单审批通过逻辑，若工单处于最后一个节点，触发工单结束逻辑；否则，触发工单流转逻辑。
     * @param approvalRecord 审批记录，已经包含前端传来数据：
     *                       work_order_id：审批的工单Id,
     *                       comment：审批留言,
     *                       approver_id：审批者（该信息从token中获取）
     *                       缺少信息：
     *                       operation：审批操作，
     *                       flow_node_id：审批节点
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void pass(ApprovalRecord approvalRecord) {
        Long workOrderId = approvalRecord.getWorkOrderId();

        //获取一个 full prepared 工单
        WorkOrderVo workOrderVo = workOrderDao.getOneWorkOrderById(workOrderId).getResult();

        if (workOrderVo == null) {
            throw new ResultCodeException("workOrder id: " + workOrderId, ResultCodeEnum.WORKORDER_NOT_EXISTS);
        }

        //检查用户是否有权限审批该工单
        checkApprovalRightOfUser(workOrderVo, approvalRecord.getApproverId());

        //检查工单是否已经结束
        checkIfWorkOrderHasFinished(workOrderVo);

        //流转
        workOrderFlower.flow(workOrderVo, approvalRecord);
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
        approvalRecordMapper.insert(approvalRecord);

        WorkOrder workOrder1 = new WorkOrder();
        //更新工单
        workOrder1.setStatus(0);
        workOrder1.setIsExamined(1);
        workOrder1.setFlowNodeId(nextFlowNodeId);

        workOrderDao.updateById(workOrder1);

        //动态决定工单的实际审批者
        //actualApproverFinalizer.decideActualApprover(workOrder);
    }


    @Override
    public ServiceResult<Page<WorkOrderVo>> listWorkOrderToBeApproved(Long visitorId, Page<WorkOrderVo> page) {
        DaoResult<Page<WorkOrderVo>> pageData = workOrderDao.getApprovalWorkOrderPage(page, visitorId);
        return new ServiceResultImpl<>(pageData.getResult());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void reject(ApprovalRecord approvalRecord) {
        Long workOrderId = approvalRecord.getWorkOrderId();

        //获取一个 full prepared 工单
        WorkOrderVo workOrderVo = workOrderDao.getOneWorkOrderById(workOrderId).getResult();

        //接下来对工单做一些安全性检测
        if (workOrderVo == null) {
            throw new ResultCodeException("workOrder id: " + workOrderId, ResultCodeEnum.WORKORDER_NOT_EXISTS);
        }

        //检查用户是否有权限审批该工单
        checkApprovalRightOfUser(workOrderVo, approvalRecord.getApproverId());

        //检查工单是否已经结束
        checkIfWorkOrderHasFinished(workOrderVo);

        //将 full prepared 工单和 full prepared 审批记录交由 finisher 处理，将工单结束。
        workOrderFinish(workOrderFinisherFactory.getFinisher("approvalTypeV2"), workOrderVo, approvalRecord, WorkOrderStatus.NOT_APPROVED, ApprovalOperation.REJECT);

    }


    /**
     * 检查接口来访者是否是工单所在节点的审批者
     *
     * @param userId      来访者id
     * @param workOrderVo 待审批工单
     */
    private void checkApprovalRightOfUser(WorkOrderVo workOrderVo, Long userId) {

        if (!workOrderVo.getActualApproverId().equals(userId)) {
            throw new ResultCodeException("user id:" + userId,
                    ResultCodeEnum.HAVE_NO_RIGHT);
        }
    }

    /**
     * 判断工单是否已经结束
     *
     * @param workOrderVo 待查询的工单
     *
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
     * 工单结束逻辑:如果是因为审批结束的，需要保存审批记录，更新工单状态并保存，生成历史工单并保存，将工单结束状态通过微信发送给工单发起者。
     *
     * @param workOrderFinisher 工单结束的任务委托给对应的Finisher即可
     * @param workOrder         待结束的工单，待完善信息：工单状态，工单是否结束，工单是否被审批。
     * @param approvalRecord    造成工单结束的审批记录，待完善信息：审批操作，审批节点id，审批时间。如果不是因为审批结束的工单，传入null
     * @param finishStatus      工单结束的原因（REJECT或PASS或revoke或invalidation），以及工单结束后的状态
     * @param approvalOperation 如果工单结束是因为审批操作，那么传入对应的审批操作枚举值，否则传入null即可
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void workOrderFinish(WorkOrderFinisher workOrderFinisher, WorkOrder workOrder, ApprovalRecord approvalRecord, WorkOrderStatus finishStatus,
                                ApprovalOperation approvalOperation) {

        WorkOrderFinishWrapper workOrderFinishWrapper = new WorkOrderFinishWrapper();
        workOrderFinishWrapper.setApprovalRecord(approvalRecord);
        workOrderFinishWrapper.setFullPreparedWorkOrderToBeFinished(workOrder);
        workOrderFinishWrapper.setFinishStatusConstant(finishStatus);
        workOrderFinisher.finishWorkOrder(workOrderFinishWrapper);

    }

    public void prepareApprovalRecord(ApprovalRecord approvalRecord, Long flowNodeId, ApprovalOperation approvalOperation) {
        approvalRecord.setFlowNodeId(flowNodeId);
        approvalRecord.setOperation(approvalOperation.getCode());
    }
}
