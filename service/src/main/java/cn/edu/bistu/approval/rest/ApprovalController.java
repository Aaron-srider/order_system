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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/approval/pass")
    public Result pass(@RequestBody ApprovalRecord approvalRecord,
                       HttpServletRequest req) throws NoSuchFieldException, IllegalAccessException {
        //获取审批者id
        Long approverId = getVisitorId(req);

        //生成审批记录
        approvalRecord.setApproverId(approverId);


        approvalService.pass(approvalRecord);

        return Result.ok();
    }

    @PostMapping("/approval/reject")
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

    @PostMapping("/approval/list")
    public Result list(@RequestBody Map<String, Object> page, HttpServletRequest req) throws NoSuchFieldException, IllegalAccessException {

        MapService optional = MapService.map()
                .putMap("size", 10)
                .putMap("current", 1)
                .putMap("title", "");
        paramIntegrityChecker.setOptionalPropsName(optional);
        paramIntegrityChecker.checkMapParamIntegrity(page);

        WorkOrderVo workOrderVo = new WorkOrderVo();
        Integer size = (Integer)page.get("size");
        Integer current = (Integer)page.get("current");
        workOrderVo.setSize(size.longValue());
        workOrderVo.setCurrent(current.longValue());
        Object title = page.get("title");
        if(title != null) {
            workOrderVo.setTitle((String)title);
        }

        ServiceResult<Page<JSONObject>> serviceResult = approvalService.listWorkOrderToBeApproved(getVisitorId(req), workOrderVo);
        Page<JSONObject> result = serviceResult.getServiceResult();
        return Result.ok(result);
    }



}
