package cn.edu.bistu.approval;

import cn.edu.bistu.constants.WorkOrderStatus;
import cn.edu.bistu.model.entity.ApprovalRecord;
import cn.edu.bistu.model.entity.WorkOrder;
import lombok.Data;

@Data
public class WorkOrderFinishContext {

    /**
     * 要结束的工单，必填
     */
    WorkOrder fullPreparedWorkOrderToBeFinished;

    /**
     * 如果工单因为审批而结束，该参数必传，否则填空
     */
    ApprovalRecord approvalRecord;

    /**
     * 工单结束的原因，必填
     */
    WorkOrderStatus finishStatusConstant;

    public static WorkOrderFinishContext getFullInstance(WorkOrder fullPreparedWorkOrderToBeFinished,
                                                         ApprovalRecord approvalRecord,
                                                         WorkOrderStatus finishStatusConstant) {
        WorkOrderFinishContext workOrderFinishContext = new WorkOrderFinishContext();
        workOrderFinishContext.setFinishStatusConstant(finishStatusConstant);
        workOrderFinishContext.setFullPreparedWorkOrderToBeFinished(fullPreparedWorkOrderToBeFinished);
        workOrderFinishContext.setApprovalRecord(approvalRecord);
        return workOrderFinishContext;
    }
}
