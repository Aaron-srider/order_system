package cn.edu.bistu.approval.rest;

import cn.edu.bistu.approval.service.ApprovalService;
import cn.edu.bistu.common.MapService;
import cn.edu.bistu.common.config.ParamIntegrityChecker;
import cn.edu.bistu.common.rest.BaseController;
import cn.edu.bistu.flow.service.FlowNodeService;
import cn.edu.bistu.model.common.Result;
import cn.edu.bistu.model.common.ServiceResult;
import cn.edu.bistu.model.entity.ApprovalRecord;
import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.model.vo.WorkOrderVo;
import cn.edu.bistu.workOrder.service.WorkOrderService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    ParamIntegrityChecker paramIntegrityChecker;

    @PutMapping("/approval/pass")
    public Result pass(@RequestBody ApprovalRecord approvalRecord,
                       HttpServletRequest req) throws NoSuchFieldException, IllegalAccessException {
        //获取审批者id
        Long approverId = getVisitorId(req);

        //生成审批记录
        approvalRecord.setApproverId(approverId);


        approvalService.pass(approvalRecord);

        return Result.ok();
    }

    @PutMapping("/approval/reject")
    public Result reject(@RequestBody Map<String, Object> paramMap,
                         HttpServletRequest req) throws NoSuchFieldException, IllegalAccessException {

        paramIntegrityChecker.setRequiredPropsName(new String[]{"workOrderId"});
        MapService optional = MapService.map().putMap("comment", "");
        paramIntegrityChecker.setOptionalPropsName(optional);
        paramIntegrityChecker.checkMapParamIntegrity(paramMap);

        //生成审批记录
        ApprovalRecord approvalRecord = new ApprovalRecord();
        approvalRecord.setComment((String)paramMap.get("comment"));
        approvalRecord.setWorkOrderId(((Integer)paramMap.get("workOrderId")).longValue());
        Long approverId = getVisitorId(req);
        approvalRecord.setApproverId(approverId);

        approvalService.reject(approvalRecord);

        return Result.ok();
    }

    @GetMapping("/approval/workOrders")
    public Result list(@RequestParam(required = false) Integer size,
                       @RequestParam(required = false) Integer current,
                       @RequestParam(required = false) String title,
                       HttpServletRequest req) throws NoSuchFieldException, IllegalAccessException {

        //MapService optional = MapService.map()
        //        .putMap("size", 10)
        //        .putMap("current", 1)
        //        .putMap("title", "");
        //paramIntegrityChecker.setOptionalPropsName(optional);
        //paramIntegrityChecker.checkMapParamIntegrity(page);

        if (size == null) {
            size = 10;
        }
        if (current == null) {
            current = 1;
        }
        if (title == null) {
            title = "";
        }

        WorkOrderVo workOrderVo = new WorkOrderVo();
        //Integer size = (Integer)page.get("size");
        //Integer current = (Integer)page.get("current");
        workOrderVo.setSize(size.longValue());
        workOrderVo.setCurrent(current.longValue());
        //Object title = page.get("title");
        workOrderVo.setTitle(title);

        ServiceResult<Page<JSONObject>> serviceResult = approvalService.listWorkOrderToBeApproved(getVisitorId(req), workOrderVo);
        Page<JSONObject> result = serviceResult.getServiceResult();
        return Result.ok(result);
    }



}
