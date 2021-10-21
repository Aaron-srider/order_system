package cn.edu.bistu.admin.workOrder.mapper;

import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.workOrder.dao.WorkOrderDao;

import java.util.List;

/**
 * 负责后台管理工单的Dao操作
 */
public interface AdminWorkOrderDao extends WorkOrderDao {

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

    /**
     * 根据id修改工单的标题和内容
     * @param workOrder 有效载荷：id/title/content
     */
    public void updateWorkOrderById(WorkOrder workOrder);

    /**
     * 将指定工单作废
     * @param id 工单id
     */
    public void changeWorkOrderStatusToInvalidation(Long id);


}
