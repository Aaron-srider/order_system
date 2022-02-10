package cn.edu.bistu.approval;

import cn.edu.bistu.model.entity.ApprovalRecord;
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
    public void finishWorkOrder(WorkOrderFinishContext workOrderFinishWrap) {

        ApprovalRecord approvalRecord = workOrderFinishWrap.approvalRecord;
        //插入审批记录
        approvalRecordMapper.insert(approvalRecord);

        finishWorkOrder0(workOrderFinishWrap);
    }

}
