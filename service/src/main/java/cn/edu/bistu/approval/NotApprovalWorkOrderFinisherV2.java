package cn.edu.bistu.approval;

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
    public void finishWorkOrder(WorkOrderFinishContext workOrderFinishWrap) {
        finishWorkOrder0(workOrderFinishWrap);
    }


}
