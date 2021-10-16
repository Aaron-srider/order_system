package cn.edu.bistu.workOrder.service;

import cn.edu.bistu.model.common.result.ServiceResult;
import cn.edu.bistu.model.entity.WorkOrderHistory;
import cn.edu.bistu.model.vo.WorkOrderHistoryVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface WorkOrderHistoryService extends IService<WorkOrderHistory>{

    ServiceResult listWorkOrderHistory(WorkOrderHistoryVo workOrderHistoryVo, Page<WorkOrderHistoryVo> page);

    ServiceResult<WorkOrderHistoryVo> detail(WorkOrderHistory workOrderHistory, long visitorId);
}
