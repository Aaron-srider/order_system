package cn.edu.bistu.approval;

import cn.edu.bistu.constants.ApprovalOperation;
import cn.edu.bistu.constants.WorkOrderStatus;
import cn.edu.bistu.model.entity.ApprovalRecord;
import cn.edu.bistu.model.entity.WorkOrder;
import org.springframework.stereotype.Component;

@Component
public class ApprovalTypeFinisher extends BaseWorkOrderFinisher implements WorkOrderFinisher {

    @Override
    public String getType() {
        return "approvalType";
    }

    @Override
    public void finishWorkOrder(WorkOrder workOrder, ApprovalRecord approvalRecord, WorkOrderStatus finishStatus,
                                ApprovalOperation approvalOperation) {

        prepareApprovalRecord(approvalRecord, workOrder.getFlowNodeId(), approvalOperation);

        //工单已经被审批过
        workOrder.setIsExamined(1);

        approvalRecordMapper.insert(approvalRecord);

        commonRoutine(workOrder, finishStatus);
    }
}
