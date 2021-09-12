package cn.edu.bistu.approval.rest;

import cn.edu.bistu.approval.service.ApprovalService;
import cn.edu.bistu.common.MapService;
import cn.edu.bistu.common.config.ParamIntegrityChecker;
import cn.edu.bistu.common.rest.BaseController;
import cn.edu.bistu.flow.service.FlowNodeService;
import cn.edu.bistu.model.common.Result;
import cn.edu.bistu.model.common.ServiceResult;
import cn.edu.bistu.model.common.validation.Insert;
import cn.edu.bistu.model.common.validation.Update;
import cn.edu.bistu.model.entity.ApprovalRecord;
import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.model.entity.auth.User;
import cn.edu.bistu.model.vo.PageVo;
import cn.edu.bistu.model.vo.WorkOrderVo;
import cn.edu.bistu.workOrder.service.WorkOrderService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Slf4j
@RestController
public class ApprovalController extends BaseController {

    @Autowired
    ApprovalService approvalService;

    @Autowired
    FlowNodeService flowNodeService;

    @Autowired
    WorkOrderService workOrderService;

    @PutMapping("/approval/pass")
    public Result pass(@RequestBody @Validated(Update.class) ApprovalRecord approvalRecord,
                       HttpServletRequest req) throws NoSuchFieldException, IllegalAccessException {
        //获取审批者id
        Long approverId = getVisitorId(req);

        if (approvalRecord.getComment() == null) {
            approvalRecord.setComment("");
        }

        //生成审批记录
        approvalRecord.setApproverId(approverId);

        approvalService.pass(approvalRecord);

        return Result.ok();
    }

    @PutMapping("/approval/reject")
    public Result reject(@RequestBody @Validated(Update.class) ApprovalRecord approvalRecord,
                         HttpServletRequest req) throws NoSuchFieldException, IllegalAccessException {

        //获取审批者id
        Long approverId = getVisitorId(req);

        if (approvalRecord.getComment() == null) {
            approvalRecord.setComment("");
        }

        approvalRecord.setApproverId(approverId);

        approvalService.reject(approvalRecord);

        return Result.ok();
    }

    @GetMapping("/approval/workOrders")
    public Result list(PageVo pageVo,
                       WorkOrder workOrder,
                       HttpServletRequest req) throws NoSuchFieldException, IllegalAccessException {

        if (pageVo.getSize() == null) {
            pageVo.setSize(10);
        }
        if (pageVo.getCurrent() == null) {
            pageVo.setCurrent(1);
        }
        if (workOrder.getTitle() == null) {
            workOrder.setTitle("");
        }

        Page<WorkOrder> page = new Page<>(pageVo.getCurrent(), pageVo.getSize());

        ServiceResult<Page<JSONObject>> serviceResult = approvalService.listWorkOrderToBeApproved(getVisitorId(req), page, workOrder);
        Page<JSONObject> result = serviceResult.getServiceResult();
        return Result.ok(result);
    }


}
