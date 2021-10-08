package cn.edu.bistu.common.utils;

import cn.edu.bistu.approval.mapper.ApprovalRecordMapper;
import cn.edu.bistu.constants.ApprovalOperation;
import cn.edu.bistu.constants.WorkOrderStatus;
import cn.edu.bistu.model.entity.ApprovalRecord;
import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.workOrder.mapper.WorkOrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WorkOrderUtils {

    @Autowired
    WorkOrderDao workOrderDao;

    @Autowired
    ApprovalRecordMapper approvalRecordMapper;



    /**
     * 工单结束逻辑:如果是因为审批结束的，需要保存审批记录，更新工单状态并保存，生成历史工单并保存，将工单结束状态通过微信发送给工单发起者。
     *
     * @param workOrder      待结束的工单，待完善信息：工单状态，工单是否结束，工单是否被审批。
     * @param approvalRecord 造成工单结束的审批记录，待完善信息：审批操作，审批节点id，审批时间。如果不是因为审批结束的工单，传入null
     * @param finishStatus 工单结束的原因（REJECT或PASS或revoke或invalidation），以及工单结束后的状态
     */
    public void workOrderFinish(WorkOrder workOrder, ApprovalRecord approvalRecord, WorkOrderStatus finishStatus) {

        //判断是否需要保存审批记录
        if(finishStatus.equals(WorkOrderStatus.NOT_APPROVED) || finishStatus.equals(WorkOrderStatus.COMPLETED_SUCCESSFULLY)) {
            //工单已经被审批过
            workOrder.setIsExamined(1);
            //保存审批记录
            if (finishStatus.equals(WorkOrderStatus.NOT_APPROVED)) {
                prepareApprovalRecord(approvalRecord, workOrder.getFlowNodeId(), ApprovalOperation.REJECT);
            } else {
                prepareApprovalRecord(approvalRecord, workOrder.getFlowNodeId(), ApprovalOperation.PASS);
            }
            approvalRecordMapper.insert(approvalRecord);
        }

        //记录工单结束前的状态
        cn.edu.bistu.model.entity.WorkOrderStatus beforeFinished = new cn.edu.bistu.model.entity.WorkOrderStatus();
        beforeFinished.setValue(workOrder.getStatus());

        //更新工单状态
        cn.edu.bistu.model.entity.WorkOrderStatus workOrderStatus = workOrderDao.constantToEntity(finishStatus);
        workOrder.setStatus(workOrderStatus.getValue());
        workOrder.setIsFinished(1);     //工单结束
        workOrderDao.getWorkOrderMapper().updateById(workOrder);

        //生成历史工单
        workOrderDao.generateWorkOrderHistory(beforeFinished, workOrder);

        //发送微信通知
        //Long initiatorId = workOrder.getInitiatorId();
        //UserVo userVo = userMapper.getOneById(initiatorId);
        //String openId = userVo.getOpenId();
        ////模板还没选好，此步跳过
        //wxMiniApi.sendSubscribeMsg(openId);

    }

    public void prepareApprovalRecord(ApprovalRecord approvalRecord, Long flowNodeId, ApprovalOperation approvalOperation) {
        approvalRecord.setFlowNodeId(flowNodeId);
        approvalRecord.setOperation(approvalOperation.getCode());
    }
}
