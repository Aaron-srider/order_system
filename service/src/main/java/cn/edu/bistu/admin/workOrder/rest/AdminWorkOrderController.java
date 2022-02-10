package cn.edu.bistu.admin.workOrder.rest;

import cn.edu.bistu.admin.workOrder.service.AdminWorkOrderService;
import cn.edu.bistu.approval.service.ApprovalService;
import cn.edu.bistu.common.rest.BaseController;
import cn.edu.bistu.common.utils.Pagination;
import cn.edu.bistu.flow.service.FlowNodeService;
import cn.edu.bistu.model.common.result.ServiceResult;
import cn.edu.bistu.model.common.validation.AdminUpdate;
import cn.edu.bistu.model.vo.PageVo;
import cn.edu.bistu.model.common.result.Result;
import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.model.vo.WorkOrderVo;
import cn.edu.bistu.workOrder.service.WorkOrderHistoryService;
import cn.edu.bistu.workOrder.service.WorkOrderService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;


/**
 * 管理员操作工单接口
 */
@Slf4j
@RestController
@Validated
@CrossOrigin
public class AdminWorkOrderController extends BaseController {

    @Autowired
    ApprovalService approvalRecordService;

    @Autowired
    WorkOrderService workOrderService;

    @Autowired
    WorkOrderHistoryService workOrderHistorService;

    @Autowired
    FlowNodeService flowNodeService;

    @Autowired
    AdminWorkOrderService adminWorkOrderService;

    /**
     * 管理员查询所有的工单信息
     *
     * @param current 页数
     * @param size 页面大小
     */
    @GetMapping("/admin/workOrders")
    public Result workOrders(
            Integer current,
            Integer size,
            WorkOrderVo workOrderVo
    ) throws NoSuchFieldException, IllegalAccessException {
        PageVo pageVo = Pagination.setDefault(current, size);
        Page<WorkOrderVo> page = new Page<>(pageVo.getCurrent(), pageVo.getSize());
        ServiceResult serviceResult = workOrderService.getAllWorkOrders(page, workOrderVo);
        return Result.ok(serviceResult.getServiceResult());
    }


    /**
     * 管理员更新一条工单
     * @param id 工单id
     * @param workOrder 要更新的工单信息
     * @return
     */
    @PutMapping("/admin/workOrder/{id}")
    public Result updateWorkOrder(
            @PathVariable @NotNull Long id,
            @RequestBody @Validated({AdminUpdate.class}) WorkOrder workOrder) {
        workOrder.setId(id);
        adminWorkOrderService.updateWorkOrderByWorkOrderId(workOrder);
        return Result.ok();
    }

    /**
     * 管理员批量删除工单
     * @param listMap 待删除的工单ID
     */
    @DeleteMapping("/admin/workOrders")
    public Result deleteWorkOrders(@RequestBody Map<String, List<Long>> listMap) {
        List<Long> workOrderIdList = listMap.get("idList");
        adminWorkOrderService.deleteWorkOrdersByWorkOrderIdList(workOrderIdList);
        return Result.ok();
    }

    /**
     * 管理员批量作废工单
     * @param listMap 待作废的工单ID
     */
    @PutMapping("/admin/workOrder/cancellation")
    public Result invalidWorkOrder(@RequestBody Map<String, List<Long>> listMap) throws NoSuchFieldException, IllegalAccessException {
        List<Long> workOrderIdList = listMap.get("idList");
        adminWorkOrderService.invalidationWorkOrder(workOrderIdList);
        return Result.ok();
    }

    /**
     * 管理员批量开启工单
     * @param listMap 待开启的工单ID
     */
    @PutMapping("/admin/workOrder/enablement")
    public Result enableWorkOrder(@RequestBody Map<String, List<Long>> listMap) throws NoSuchFieldException, IllegalAccessException {
        List<Long> workOrderIdList = listMap.get("idList");
        adminWorkOrderService.enableWorkOrder(workOrderIdList);
        return Result.ok();
    }

}
