package cn.edu.bistu.admin.workOrder.service;

import cn.edu.bistu.model.entity.WorkOrder;

import java.util.List;

public interface AdminWorkOrderService {


    /**
     * 根据Id列表删除所有的工单，并级联删除工单对应的历史记录和审批记录
     * @param workOrderIdList 待删除的工单id列表
     */
    public void deleteWorkOrdersByWorkOrderIdList(List<Long> workOrderIdList);

    /**
     * 根据id修改工单的标题和内容
     * @param workOrder 有效载荷：id/title/content
     */
    public void updateWorkOrderByWorkOrderId(WorkOrder workOrder);


    /**
     * 将指定工单作废
     * @param id 工单id
     */
    public void invalidationWorkOrder(Long id);

}
