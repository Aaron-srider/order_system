package cn.edu.bistu.approval;

import cn.edu.bistu.constants.ApprovalOperation;
import cn.edu.bistu.constants.WorkOrderStatus;
import cn.edu.bistu.model.entity.ApprovalRecord;
import cn.edu.bistu.model.entity.WorkOrder;

public interface WorkOrderFinisher {

    public String getType();

    /**
     * 结束工单的具体算法
     * @param workOrder 待结束的具体工单
     * @param approvalRecord 工单结束时的审批记录，如果不是因为审批导致的结束，该参数应该为空
     * @param finishStatus 工单结束时的状态，是枚举类WorkOrderStatus的对象
     * @param approvalOperation 如果工单结束是因为审批操作，那么传入对应的审批操作枚举值，否则传入null即可
     */
    public void finishWorkOrder(WorkOrder workOrder, ApprovalRecord approvalRecord, WorkOrderStatus finishStatus,
                                ApprovalOperation approvalOperation);
}
