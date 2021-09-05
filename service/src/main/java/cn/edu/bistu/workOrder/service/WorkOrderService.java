package cn.edu.bistu.workOrder.service;

import cn.edu.bistu.model.common.Result;
import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.model.vo.WorkOrderVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

public interface WorkOrderService extends IService<WorkOrder>{

    IPage<WorkOrderVo> listWorkOrder(WorkOrderVo workOrderVo);


    void revoke(Long workOrderId, Long initiator);

    Result detail(WorkOrder workOrder);
}
