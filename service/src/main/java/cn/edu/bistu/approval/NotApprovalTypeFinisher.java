package cn.edu.bistu.approval;

import cn.edu.bistu.constants.ApprovalOperation;
import cn.edu.bistu.constants.WorkOrderStatus;
import cn.edu.bistu.model.entity.ApprovalRecord;
import cn.edu.bistu.model.entity.WorkOrder;
import org.springframework.stereotype.Component;


@Component
public class NotApprovalTypeFinisher extends BaseWorkOrderFinisher implements WorkOrderFinisher {

    @Override
    public String getType() {
        return "notApprovalType";
    }

    @Override
    public void finishWorkOrder(WorkOrder workOrder, ApprovalRecord approvalRecord, WorkOrderStatus finishStatus,
                                ApprovalOperation approvalOperation) {
        commonRoutine(workOrder, finishStatus);
    }
}
