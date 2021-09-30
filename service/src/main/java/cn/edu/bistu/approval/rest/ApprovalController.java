package cn.edu.bistu.approval.rest;

import cn.edu.bistu.approval.service.ApprovalService;
import cn.edu.bistu.common.BeanUtils;
import cn.edu.bistu.common.rest.BaseController;
import cn.edu.bistu.common.utils.Pagination;
import cn.edu.bistu.flow.service.FlowNodeService;
import cn.edu.bistu.model.common.result.Result;
import cn.edu.bistu.model.common.result.ServiceResult;
import cn.edu.bistu.model.common.validation.Update;
import cn.edu.bistu.model.entity.ApprovalRecord;
import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.model.vo.PageVo;
import cn.edu.bistu.workOrder.service.WorkOrderService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

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

        setUpApprovalRecord(req, approvalRecord);

        approvalService.pass(approvalRecord);

        return Result.ok();
    }

    @PutMapping("/approval/reject")
    public Result reject(@RequestBody @Validated(Update.class) ApprovalRecord approvalRecord,
                         HttpServletRequest req) throws NoSuchFieldException, IllegalAccessException {
        
        setUpApprovalRecord(req, approvalRecord);

        approvalService.reject(approvalRecord);

        return Result.ok();
    }

    @GetMapping("/approval/workOrders")
    public Result list(PageVo pageVo,
                       WorkOrder workOrder,
                       HttpServletRequest req) throws NoSuchFieldException, IllegalAccessException {

        pageVo = Pagination.setDefault(pageVo.getCurrent(), pageVo.getSize());

        if (BeanUtils.isEmpty(workOrder.getTitle())) {
            workOrder.setTitle("");
        }

        Page<WorkOrder> page = new Page<>(pageVo.getCurrent(), pageVo.getSize());

        ServiceResult<Page<JSONObject>> serviceResult = approvalService.listWorkOrderToBeApproved(getVisitorId(req), page, workOrder);
        Page<JSONObject> result = serviceResult.getServiceResult();
        return Result.ok(result);
    }

    public void setUpApprovalRecord(HttpServletRequest req, ApprovalRecord approvalRecord) {
        //获取审批者id
        Long approverId = getVisitorId(req);
        approvalRecord.setApproverId(approverId);

        //设置审批内容
        if (BeanUtils.isEmpty(approvalRecord.getComment()) ) {
            approvalRecord.setComment("");
        }
    }


}
