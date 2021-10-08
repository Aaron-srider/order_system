package cn.edu.bistu.admin.workOrder.service;

import cn.edu.bistu.model.entity.WorkOrder;

import java.util.ArrayList;
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
     * 将指定工单作废，并生成历史工单，注意，只有在审工单才能作废，作废工单进入结束状态
     * @param idList 工单idList
     */
    public void invalidationWorkOrder(List<Long> idList) throws NoSuchFieldException, IllegalAccessException;

    /**
     * 将指定工单重新开启，并生成删除工单历史，注意，只有作废的工单才能再次开启
     * @param idList 工单idList
     */
    public void enableWorkOrder(List<Long> idList) throws NoSuchFieldException, IllegalAccessException;

}
