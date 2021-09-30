package cn.edu.bistu.workOrder.service;

import cn.edu.bistu.model.common.result.ServiceResult;
import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.model.vo.AdminWorkOrderQueryVo;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface WorkOrderService extends IService<WorkOrder>{

    ServiceResult<JSONObject> listWorkOrder(WorkOrder workOrderVo, Page<WorkOrder> page) throws NoSuchFieldException, IllegalAccessException;

    void revoke(Long workOrderId, Long initiator);

    ServiceResult<JSONObject> detail(WorkOrder workOrder) throws NoSuchFieldException, IllegalAccessException;

    void submitWorkOrder(WorkOrder workOrder);

    ServiceResult getAllWorkOrders(Page<WorkOrder> page, AdminWorkOrderQueryVo adminWorkOrderQueryVo) throws NoSuchFieldException, IllegalAccessException;

    void deleteAttachmentByWorkOrderId(Long workOrderId);
}
