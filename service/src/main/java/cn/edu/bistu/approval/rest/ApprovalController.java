package cn.edu.bistu.approval.rest;

import cn.edu.bistu.approval.service.ApprovalService;
import cn.edu.bistu.common.MapService;
import cn.edu.bistu.common.config.ValidationWrapper;
import cn.edu.bistu.common.exception.ParameterMissing;
import cn.edu.bistu.common.exception.ParameterRedundent;
import cn.edu.bistu.constants.ResultCodeEnum;
import cn.edu.bistu.flow.service.FlowNodeService;
import cn.edu.bistu.model.common.Result;
import cn.edu.bistu.model.entity.ApprovalRecord;
import cn.edu.bistu.model.entity.FlowNode;
import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.model.vo.WorkOrderVo;
import cn.edu.bistu.workOrder.service.WorkOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
public class ApprovalController {

    @Autowired
    ApprovalService approvalService;

    @Autowired
    FlowNodeService flowNodeService;

    @Autowired
    WorkOrderService workOrderService;

    @Autowired
    ValidationWrapper globalValidator;

    @PostMapping("/approval/pass")
    public Result pass(@RequestBody ApprovalRecord approvalRecord,
                       HttpServletRequest req) {
        MapService mapService = (MapService) req.getAttribute("userInfo");
        Long approverId = mapService.getVal("id", Long.class);

        approvalRecord.setApproverId(approverId);
        Long workOrderId = approvalRecord.getWorkOrderId();

        Map<String, Object> map = approvalService.isLastNode(workOrderId);
        boolean isLastNode = (boolean) map.get("isLastNode");

        FlowNode currentNode = (FlowNode) map.get("currentNode");
        approvalRecord.setFlowNodeId(currentNode.getId());

        //如果不是最后一个节点，将工单移动至下一个节点
        if (!isLastNode) {
            approvalService.pass(approvalRecord, currentNode.getNextId());
        }
        //如果是最后一个节点，工单结束
        else {
            approvalService.finish(approvalRecord);
        }

        return Result.ok();
    }

    @PostMapping("/approval/reject")
    public Result reject(@RequestBody ApprovalRecord approvalRecord,
                         HttpServletRequest req) {

        try {
            globalValidator.setRequiredPropsName(new String[]{"workOrderId"});
            globalValidator.setOptionalPropsName(new String[]{"comment"});
            globalValidator.checkParamIntegrity(approvalRecord);
        } catch (ParameterMissing e) {
            log.debug("missing props:" + e.getMissingParams());
            return Result.build(e.getMissingParams(), ResultCodeEnum.FRONT_DATA_MISSING);
        } catch (ParameterRedundent e) {
            log.debug("missing props:" + e.getRedundentParams());
            return Result.build(e.getRedundentParams(), ResultCodeEnum.FRONT_DATA_REDUNDANT);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            globalValidator.setPropsNameNull();
        }

        MapService mapService = (MapService) req.getAttribute("userInfo");
        Long approverId = mapService.getVal("id", Long.class);

        approvalRecord.setApproverId(approverId);

        approvalService.reject(approvalRecord);

        return Result.ok();
    }


}
