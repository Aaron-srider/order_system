package cn.edu.bistu.workOrder.service;

import cn.edu.bistu.model.common.Result;
import cn.edu.bistu.model.entity.WorkOrderHistory;
import cn.edu.bistu.model.vo.WorkOrderHistoryVo;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface WorkOrderHistoryService extends IService<WorkOrderHistory>{

    Page<JSONObject> listWorkOrderHistory(WorkOrderHistory workOrderHistory, Page<WorkOrderHistory> page) throws NoSuchFieldException, IllegalAccessException;

    JSONObject detail(WorkOrderHistory workOrderHistory) throws NoSuchFieldException, IllegalAccessException;
}
