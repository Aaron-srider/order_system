package cn.edu.bistu.workOrder.service;

import cn.edu.bistu.model.common.ServiceResult;
import cn.edu.bistu.model.common.ServiceResultImpl;
import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.model.vo.WorkOrderVo;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface WorkOrderService extends IService<WorkOrder>{

    ServiceResult<JSONObject> listWorkOrder(WorkOrder workOrderVo, Page<WorkOrder> page) throws NoSuchFieldException, IllegalAccessException;

    void revoke(Long workOrderId, Long initiator);

    ServiceResult<JSONObject>  detail(WorkOrder workOrder) throws NoSuchFieldException, IllegalAccessException;

    void submitWorkOrder(WorkOrderVo workOrderVo);
}
