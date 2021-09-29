package cn.edu.bistu.admin.workOrder.service;

import java.util.List;

public interface AdminWorkOrderService {


    /**
     * 根据Id列表删除所有的工单，并级联删除工单对应的历史记录和审批记录
     * @param workOrderIdList 待删除的工单id列表
     */
    public void deleteWorkOrdersByWorkOrderIdList(List<Long> workOrderIdList);

}
