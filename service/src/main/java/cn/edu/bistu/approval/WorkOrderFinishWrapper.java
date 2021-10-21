package cn.edu.bistu.approval;

import cn.edu.bistu.constants.WorkOrderStatus;
import cn.edu.bistu.model.entity.ApprovalRecord;
import cn.edu.bistu.model.entity.WorkOrder;
import lombok.Data;

@Data
public class WorkOrderFinishWrapper {
    WorkOrder fullPreparedWorkOrderToBeFinished;
    ApprovalRecord approvalRecord;
    WorkOrderStatus finishStatusConstant;
}
