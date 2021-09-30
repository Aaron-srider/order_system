package cn.edu.bistu.approval.service;

import cn.edu.bistu.approval.mapper.ApprovalRecordMapper;
import cn.edu.bistu.common.BeanUtils;
import cn.edu.bistu.common.exception.ResultCodeException;
import cn.edu.bistu.constants.ApprovalOperation;
import cn.edu.bistu.constants.ResultCodeEnum;
import cn.edu.bistu.constants.WorkOrderStatus;
import cn.edu.bistu.flow.mapper.FlowDao;
import cn.edu.bistu.model.common.result.DaoResult;
import cn.edu.bistu.model.common.result.ServiceResult;
import cn.edu.bistu.model.common.result.ServiceResultImpl;
import cn.edu.bistu.model.entity.ApprovalRecord;
import cn.edu.bistu.model.entity.FlowNode;
import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.model.entity.WorkOrderHistory;
import cn.edu.bistu.workOrder.mapper.WorkOrderDao;
import cn.edu.bistu.workOrder.service.WorkOrderHistoryService;
import cn.edu.bistu.wx.service.WxMiniApi;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Slf4j
@Service
public class ApprovalServiceImpl implements ApprovalService {

    @Autowired
    FlowDao flowDao;

    @Autowired
    WorkOrderDao workOrderDao;

    @Autowired
    ApprovalRecordMapper approvalRecordMapper;

    @Autowired
    WorkOrderHistoryService workOrderHistoryService;

    @Autowired
    WxMiniApi wxMiniApi;

    /**
     * 工单审批通过逻辑，若工单处于最后一个节点，触发工单结束逻辑；否则，触发工单流转逻辑。
     *
     * @param approvalRecord 审批记录
     */
    @Transactional
    @Override
    public void pass(ApprovalRecord approvalRecord) throws NoSuchFieldException, IllegalAccessException {
        Long workOrderId = approvalRecord.getWorkOrderId();

        //检查用户是否有权限审批该工单
        checkApprovalRightOfUser(approvalRecord.getApproverId(), workOrderId);

        //检查工单是否已经结束
        WorkOrder workOrder = checkIfWorkOrderHasFinished(workOrderId);

        //查看工单下一个审批节点，没有下一个节点时返回null
        Long nextFlowNodeId = flowDao.getNextFlowNode(workOrder.getFlowNodeId());

        //工单处于最后一个审批节点，工单结束
        if (nextFlowNodeId==null) {
            workOrderFinish(workOrder, approvalRecord, WorkOrderStatus.COMPLETED_SUCCESSFULLY);
        }
        //工单流转
        else {
            WorkOrderFlowToNext(workOrder, approvalRecord, nextFlowNodeId);
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
        workOrderDao.getWorkOrderMapper().updateById(workOrder);
    }

    private void prepareApprovalRecord(ApprovalRecord approvalRecord, Long flowNodeId, ApprovalOperation approvalOperation) {
        approvalRecord.setFlowNodeId(flowNodeId);
        approvalRecord.setOperation(approvalOperation.getCode());
    }

    /**
     * 工单结束逻辑，更新工单状态并保存，生成历史工单并保存，保存审批记录，将工单结束状态通过微信发送给工单发起者。
     *
     * @param workOrder      待结束的工单，待完善信息：工单状态，工单是否结束，工单是否被审批。
     * @param approvalRecord 造成工单结束的审批记录，待完善信息：审批操作，审批节点id，审批时间。
     * @param finishStatus 工单结束时的状态（REJECT或PASS）
     */
    private void workOrderFinish(WorkOrder workOrder, ApprovalRecord approvalRecord, WorkOrderStatus finishStatus) {
        //保存审批记录
        if (finishStatus.equals(WorkOrderStatus.NOT_APPROVED)) {
            prepareApprovalRecord(approvalRecord, workOrder.getFlowNodeId(), ApprovalOperation.REJECT);
        } else {
            prepareApprovalRecord(approvalRecord, workOrder.getFlowNodeId(), ApprovalOperation.PASS);
        }
        approvalRecordMapper.insert(approvalRecord);

        List<cn.edu.bistu.model.entity.WorkOrderStatus> workOrderStatusFromDateBase = workOrderDao.getWorkOrderStatusMapper().selectList(null);

        //更新工单状态
        if (finishStatus.equals(WorkOrderStatus.NOT_APPROVED)) {
            for (cn.edu.bistu.model.entity.WorkOrderStatus workOrderStatus : workOrderStatusFromDateBase) {
                if (workOrderStatus.getAlias().equals(WorkOrderStatus.NOT_APPROVED.toString())) {
                    workOrder.setStatus(workOrderStatus.getValue());
                    break;
                }
            }
        } else if (finishStatus.equals(WorkOrderStatus.COMPLETED_SUCCESSFULLY)) {
            for (cn.edu.bistu.model.entity.WorkOrderStatus workOrderStatus : workOrderStatusFromDateBase) {
                if (workOrderStatus.getAlias().equals(WorkOrderStatus.COMPLETED_SUCCESSFULLY.toString())) {
                    workOrder.setStatus(workOrderStatus.getValue());
                    break;
                }
            }
        }

        workOrder.setIsFinished(1);     //工单结束
        workOrder.setIsExamined(1);     //工单已经被审批过
        workOrderDao.getWorkOrderMapper().updateById(workOrder);
        log.debug("workOrder to be updated:" + workOrder);

        //生成历史工单
        WorkOrderHistory workOrderHistory = new WorkOrderHistory();
        BeanUtils.copyProperties(workOrder, workOrderHistory);
        workOrderHistory.setWorkOrderId(workOrder.getId());
        log.debug("workOrderHistory to be saved:" + workOrderHistory);
        workOrderHistoryService.save(workOrderHistory);

        //发送微信通知
        //Long initiatorId = workOrder.getInitiatorId();
        //UserVo userVo = userMapper.getOneById(initiatorId);
        //String openId = userVo.getOpenId();
        ////模板还没选好，此步跳过
        //wxMiniApi.sendSubscribeMsg(openId);

    }

    @Override
    public ServiceResult<Page<JSONObject>> listWorkOrderToBeApproved(Long visitorId, Page<WorkOrder> page, WorkOrder workOrder) throws NoSuchFieldException, IllegalAccessException {

        DaoResult<Page<JSONObject>> pageData = workOrderDao.getApprovalWorkOrderPage(page, visitorId, workOrder);
        Page<JSONObject> result = pageData.getResult();

        ServiceResult<Page<JSONObject>> serviceResult = new ServiceResultImpl<>(result);
        return serviceResult;
    }

    @Transactional
    @Override
    public void reject(ApprovalRecord approvalRecord) throws NoSuchFieldException, IllegalAccessException {
        Long workOrderId = approvalRecord.getWorkOrderId();

        //检查用户是否有权限审批该工单
        checkApprovalRightOfUser(approvalRecord.getApproverId(), workOrderId);

        //检查工单是否已经结束
        WorkOrder workOrder = checkIfWorkOrderHasFinished(workOrderId);

        workOrderFinish(workOrder, approvalRecord, WorkOrderStatus.NOT_APPROVED);

    }


    /**
     * 检查接口来访者是否是工单所在节点的审批者
     * @param userId 来访者id
     * @param workOrderId 审批工单的id
     */
    private void checkApprovalRightOfUser(Long userId, Long workOrderId) throws NoSuchFieldException, IllegalAccessException {
        DaoResult<WorkOrder> daoWorkOrder = workOrderDao.getOneWorkOrderById(workOrderId);
        JSONObject detailInfo = daoWorkOrder.getDetailInfo();
        FlowNode currentFlowNode = detailInfo.getObject("currentFlowNode", FlowNode.class);
        if (!currentFlowNode.getApproverId().equals(userId)) {
            throw new ResultCodeException("user id:" + userId,
                    ResultCodeEnum.HAVE_NO_RIGHT);
        }
    }

    /**
     * 判断工单是否已经结束
     *
     * @param workOrderId 待查询的工单id
     * @return 若工单结束，抛出异常，否则返回工单
     * @throws ResultCodeException 如果工单结束，抛出该异常
     */
    private WorkOrder checkIfWorkOrderHasFinished(Long workOrderId) {

        WorkOrder workOrder = workOrderDao.getWorkOrderMapper().selectById(workOrderId);

        //若工单已结束，审批操作非法
        if (workOrder.getIsFinished().equals(1)) {
            throw new ResultCodeException(workOrder,

                    ResultCodeEnum.WORKORDER_BEEN_FINISHED);
        }

        return workOrder;

    }
}
