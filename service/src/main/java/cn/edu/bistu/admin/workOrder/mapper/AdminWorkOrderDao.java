package cn.edu.bistu.admin.workOrder.mapper;

import java.util.List;

public interface AdminWorkOrderDao {

    /**
     * 删除工单（真删除）
     * @param workOrderId 根据该id删除对应的工单
     */
    public void deleteWorkOrderByWorkOrderId(Long workOrderId);

    /**
     * 删除工单对应的历史记录（真删除）
     * @param workOrderId 根据该id删除对应的工单历史
     */
    public void deleteWorkOrderHistoryByWorkOrderId(Long workOrderId);

    /**
     * 删除工单对应的审批记录（真删除）
     * @param workOrderId 根据该id删除对应的工单审批记录
     */
    public void deleteWorkOrderApprovalRecordsByWorkOrderId(Long workOrderId);

}
