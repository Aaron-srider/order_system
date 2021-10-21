package cn.edu.bistu.approval;

import cn.edu.bistu.constants.ApprovalOperation;
import cn.edu.bistu.constants.WorkOrderStatus;
import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.model.entity.WorkOrderHistory;
import org.springframework.stereotype.Component;

/**
 * 工单结束器的具体实现类，用于在审批后结束工单
 * @author wc
 */
@Component
public class ApprovalWorkOrderFinisherV2 extends BaseWorkOrderFinisher implements WorkOrderFinisher {

    @Override
    public String getType() {
        return "approvalTypeV2";
    }

    @Override
    public void finishWorkOrder(WorkOrderFinishWrapper workOrderFinishWrap) {

        approvalRecordMapper.insert(workOrderFinishWrap.approvalRecord);

        WorkOrder workOrder1 = new WorkOrder();
        workOrder1.setId(workOrderFinishWrap.fullPreparedWorkOrderToBeFinished.getId());
        workOrder1.setIsFinished(1);
        workOrder1.setIsExamined(1);
        if (workOrderFinishWrap.approvalRecord.getOperation().equals(ApprovalOperation.PASS.getCode())) {
            workOrder1.setStatus(workOrderDao.constantToEntity(WorkOrderStatus.COMPLETED_SUCCESSFULLY).getValue());
        } else {
            workOrder1.setStatus(workOrderDao.constantToEntity(WorkOrderStatus.NOT_APPROVED).getValue());
        }
        workOrderDao.updateById(workOrder1);

        WorkOrderHistory workOrderHistory = new WorkOrderHistory();
        workOrderHistory.setWorkOrderId(workOrderFinishWrap.fullPreparedWorkOrderToBeFinished.getId());
        workOrderHistory.setBeforeFinishedStatus(workOrderFinishWrap.fullPreparedWorkOrderToBeFinished.getStatus());
        workOrderHistoryDao.insertOne(workOrderHistory);
    }
}
