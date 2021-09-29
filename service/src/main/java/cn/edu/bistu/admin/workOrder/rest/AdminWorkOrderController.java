package cn.edu.bistu.admin.workOrder.rest;

import cn.edu.bistu.admin.workOrder.service.AdminWorkOrderService;
import cn.edu.bistu.approval.service.ApprovalService;
import cn.edu.bistu.common.exception.WorkOrderNotExistsException;
import cn.edu.bistu.common.rest.BaseController;
import cn.edu.bistu.common.utils.Pagination;
import cn.edu.bistu.flow.service.FlowNodeService;
import cn.edu.bistu.model.common.result.ServiceResult;
import cn.edu.bistu.model.common.validation.AdminUpdate;
import cn.edu.bistu.model.common.validation.Insert;
import cn.edu.bistu.model.entity.WorkOrderHistory;
import cn.edu.bistu.model.vo.AdminWorkOrderQueryVo;
import cn.edu.bistu.model.vo.PageVo;
import cn.edu.bistu.workOrder.exception.AttachmentNotExistsException;
import cn.edu.bistu.common.MapService;
import cn.edu.bistu.common.config.ValidationWrapper;
import cn.edu.bistu.common.utils.MimeTypeUtils;
import cn.edu.bistu.constants.ResultCodeEnum;
import cn.edu.bistu.model.common.result.Result;
import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.workOrder.service.WorkOrderHistoryService;
import cn.edu.bistu.workOrder.service.WorkOrderService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.IOException;
import java.net.URLEncoder;
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
                             AdminWorkOrderQueryVo adminWorkOrderQueryVo
                             ) throws NoSuchFieldException, IllegalAccessException {

        PageVo pageVo = Pagination.setDefault(current, size);
        Page<WorkOrder> page = new Page<>(pageVo.getCurrent(), pageVo.getSize());
        ServiceResult serviceResult = workOrderService.getAllWorkOrders(page, adminWorkOrderQueryVo);
        return Result.ok(serviceResult.getServiceResult());
    }

    @DeleteMapping("/admin/workOrders")
    public Result deleteWorkOrders(@RequestBody Map<String, List<Long>> listMap)  {
        List<Long> workOrderIdList=listMap.get("idList");
        adminWorkOrderService.deleteWorkOrdersByWorkOrderIdList(workOrderIdList);
        return Result.ok();
    }

    @PutMapping("/admin/workOrder/{id}")
    public Result updateWorkOrder(
            @PathVariable @NotNull Long id,
            @RequestBody @Validated({AdminUpdate.class}) WorkOrder workOrder)  {
        workOrder.setId(id);
        adminWorkOrderService.updateWorkOrderByWorkOrderId(workOrder);
        return Result.ok();
    }


}
