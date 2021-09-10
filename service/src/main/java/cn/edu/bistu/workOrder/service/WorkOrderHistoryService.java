package cn.edu.bistu.workOrder.service;

import cn.edu.bistu.model.common.ServiceResult;
import cn.edu.bistu.model.common.ServiceResultImpl;
import cn.edu.bistu.model.entity.WorkOrderHistory;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface WorkOrderHistoryService extends IService<WorkOrderHistory>{

    Page<JSONObject> listWorkOrderHistory(WorkOrderHistory workOrderHistory, Page<WorkOrderHistory> page) throws NoSuchFieldException, IllegalAccessException;

    ServiceResult<JSONObject> detail(WorkOrderHistory workOrderHistory) throws NoSuchFieldException, IllegalAccessException;
}
