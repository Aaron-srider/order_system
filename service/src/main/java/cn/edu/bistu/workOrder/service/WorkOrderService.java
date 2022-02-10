package cn.edu.bistu.workOrder.service;

import cn.edu.bistu.model.common.result.ServiceResult;
import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.model.vo.WorkOrderVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface WorkOrderService extends IService<WorkOrder>{

    /**
     * 根据工单标题返回工单
     * @param workOrderVo 工单筛选条件（包含工单标题）
     * @param page 工单分页信息
     */
    ServiceResult listWorkOrder(WorkOrderVo workOrderVo, Page<WorkOrderVo> page) throws NoSuchFieldException, IllegalAccessException;

    void revoke(Long workOrderId, Long initiator);

    ServiceResult<WorkOrderVo> detail(Long workOrderId, Long visitorId);

    ServiceResult submitWorkOrder(WorkOrderVo workOrderVo);

    /**
     * 根据筛选条件查询工单列表
     *  @param page 分页信息
     * @param workOrderVo 筛选信息
     *                              startDate
     *                              endDate
     *                              studentJobId
     */
    ServiceResult getAllWorkOrders(Page<WorkOrderVo> page, WorkOrderVo workOrderVo) throws NoSuchFieldException, IllegalAccessException;

    void deleteAttachmentByWorkOrderId(Long workOrderId);


}
