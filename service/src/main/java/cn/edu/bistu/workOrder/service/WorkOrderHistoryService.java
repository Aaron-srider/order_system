package cn.edu.bistu.workOrder.service;

import cn.edu.bistu.model.common.Result;
import cn.edu.bistu.model.entity.WorkOrderHistory;
import cn.edu.bistu.model.vo.WorkOrderHistoryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

public interface WorkOrderHistoryService extends IService<WorkOrderHistory>{

    IPage<WorkOrderHistoryVo> listWorkOrderHistory(WorkOrderHistoryVo workOrderHistoryVo);

    Result detail(WorkOrderHistory workOrderHistory);
}
