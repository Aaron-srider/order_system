package cn.edu.bistu.admin.workOrder.rest;

import cn.edu.bistu.admin.workOrder.service.AdminWorkOrderService;
import cn.edu.bistu.approval.service.ApprovalService;
import cn.edu.bistu.common.rest.BaseController;
import cn.edu.bistu.common.utils.Pagination;
import cn.edu.bistu.flow.service.FlowNodeService;
import cn.edu.bistu.model.common.result.ServiceResult;
import cn.edu.bistu.model.common.validation.AdminUpdate;
import cn.edu.bistu.model.vo.AdminWorkOrderQueryVo;
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

    @GetMapping("/admin/workOrders")
    public Result workOrders(Integer current,
                             Integer size,
                             WorkOrderVo workOrderVo
                             ) throws NoSuchFieldException, IllegalAccessException {

        PageVo pageVo = Pagination.setDefault(current, size);
        Page<WorkOrderVo> page = new Page<>(pageVo.getCurrent(), pageVo.getSize());
        ServiceResult serviceResult = workOrderService.getAllWorkOrders(page, workOrderVo);
        return Result.ok(serviceResult.getServiceResult());
    }

    @PutMapping("/admin/workOrder/{id}")
    public Result updateWorkOrder(
            @PathVariable @NotNull Long id,
            @RequestBody @Validated({AdminUpdate.class}) WorkOrder workOrder)  {
        workOrder.setId(id);
        adminWorkOrderService.updateWorkOrderByWorkOrderId(workOrder);
        return Result.ok();
    }

    @DeleteMapping("/admin/workOrders")
    public Result deleteWorkOrders(@RequestBody Map<String, List<Long>> listMap)  {
        List<Long> workOrderIdList=listMap.get("idList");
        adminWorkOrderService.deleteWorkOrdersByWorkOrderIdList(workOrderIdList);
        return Result.ok();
    }

    @PutMapping("/admin/workOrder/cancellation")
    public Result invalidWorkOrder(@RequestBody Map<String, List<Long>> listMap) throws NoSuchFieldException, IllegalAccessException {
        List<Long> workOrderIdList=listMap.get("idList");
        adminWorkOrderService.invalidationWorkOrder(workOrderIdList);
        return Result.ok();
    }

    @PutMapping("/admin/workOrder/enablement")
    public Result enableWorkOrder(@RequestBody Map<String, List<Long>> listMap) throws NoSuchFieldException, IllegalAccessException {
        List<Long> workOrderIdList=listMap.get("idList");
        adminWorkOrderService.enableWorkOrder(workOrderIdList);
        return Result.ok();
    }



}
