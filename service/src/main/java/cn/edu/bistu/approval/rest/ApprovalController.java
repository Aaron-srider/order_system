package cn.edu.bistu.approval.rest;

import cn.edu.bistu.approval.service.ApprovalService;
import cn.edu.bistu.common.BeanUtils;
import cn.edu.bistu.common.MapService;
import cn.edu.bistu.common.config.ParamIntegrityChecker;
import cn.edu.bistu.common.config.ValidationWrapper;
import cn.edu.bistu.common.exception.ParameterMissingException;
import cn.edu.bistu.common.exception.ParameterRedundentException;
import cn.edu.bistu.common.rest.BaseController;
import cn.edu.bistu.constants.ResultCodeEnum;
import cn.edu.bistu.flow.service.FlowNodeService;
import cn.edu.bistu.model.common.Result;
import cn.edu.bistu.model.entity.ApprovalRecord;
import cn.edu.bistu.model.entity.FlowNode;
import cn.edu.bistu.model.vo.WorkOrderVo;
import cn.edu.bistu.workOrder.service.WorkOrderService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sun.xml.internal.xsom.impl.parser.BaseContentRef;
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
                       HttpServletRequest req) {
        //获取审批者id
        MapService mapService = (MapService) req.getAttribute("userInfo");
        Long approverId = mapService.getVal("id", Long.class);

        //生成审批记录
        approvalRecord.setApproverId(approverId);
        Long workOrderId = approvalRecord.getWorkOrderId();

        approvalService.pass(approvalRecord);

        return Result.ok();
    }

    @PostMapping("/approval/reject")
    public Result reject(@RequestBody Map<String, Object> paramMap,
                         HttpServletRequest req) {

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
    public Result list(@RequestBody Map<String, Object> page, HttpServletRequest req) {
        MapService optional = MapService.map().putMap("size", 10)
                .putMap("current", 1)
                .putMap("title", null);
        paramIntegrityChecker.setOptionalPropsName(optional);
        paramIntegrityChecker.checkMapParamIntegrity(page);

        Page<WorkOrderVo> result = approvalService.listWorkOrderToBeApproved(getVisitorId(req), page);

        Map<String, Object> resultMap = BeanUtils.bean2Map(result,
                new String[]{
                        "serialVersionUID",
                        "hitCount",
                        "optimizeCountSql",
                        "orders",
                        "isSearchCount"
                });

        return Result.ok(resultMap);
    }

}
