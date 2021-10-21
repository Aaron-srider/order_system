package cn.edu.bistu.approval;

import cn.edu.bistu.constants.ApprovalOperation;
import cn.edu.bistu.constants.WorkOrderStatus;
import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.model.entity.WorkOrderHistory;
import org.springframework.stereotype.Component;

/**
 * 工单结束器的具体实现类，用于不是因为审批而导致的工单结束，比如用户作废工单而导致的工单结束
 * @author wc
 */
@Component
public class NotApprovalWorkOrderFinisherV2 extends BaseWorkOrderFinisher implements WorkOrderFinisher {

    @Override
    public String getType() {
        return "notApprovalTypeV2";
    }

    @Override
    public void finishWorkOrder(WorkOrderFinishWrapper workOrderFinishWrap) {

        WorkOrder workOrder1 = new WorkOrder();
        workOrder1.setId(workOrderFinishWrap.fullPreparedWorkOrderToBeFinished.getId());
        workOrder1.setIsFinished(1);
        workOrder1.setIsExamined(1);
        workOrder1.setStatus(workOrderDao.constantToEntity(workOrderFinishWrap.finishStatusConstant).getValue());
        workOrderDao.updateById(workOrder1);

        WorkOrderHistory workOrderHistory = new WorkOrderHistory();
        workOrderHistory.setWorkOrderId(workOrderFinishWrap.fullPreparedWorkOrderToBeFinished.getId());
        workOrderHistory.setBeforeFinishedStatus(workOrderFinishWrap.fullPreparedWorkOrderToBeFinished.getStatus());
        workOrderHistoryDao.insertOne(workOrderHistory);
    }
}
