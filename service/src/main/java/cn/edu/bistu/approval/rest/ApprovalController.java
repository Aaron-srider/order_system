package cn.edu.bistu.approval.rest;

import cn.edu.bistu.approval.service.ApprovalService;
import cn.edu.bistu.common.MapService;
import cn.edu.bistu.flow.service.FlowNodeService;
import cn.edu.bistu.model.common.Result;
import cn.edu.bistu.model.entity.ApprovalRecord;
import cn.edu.bistu.model.entity.FlowNode;
import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.workOrder.service.WorkOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
public class ApprovalController {

    @Autowired
    ApprovalService approvalService;

    @Autowired
    FlowNodeService flowNodeService;

    @Autowired
    WorkOrderService workOrderService;

    @PostMapping("/approval/pass")
    public Result pass(@RequestBody ApprovalRecord approvalRecord,
                       HttpServletRequest req) {
        MapService mapService = (MapService) req.getAttribute("userInfo");
        Long approverId = mapService.getVal("id", Long.class);


        approvalRecord.setApproverId(approverId);
        Long workOrderId = approvalRecord.getWorkOrderId();

        Map<String,Object> map = approvalService.isLastNode(workOrderId);
        boolean isLastNode = (boolean) map.get("isLastNode");

        //如果不是最后一个节点，将工单移动至下一个节点
        if(!isLastNode) {
            FlowNode currentNode =  (FlowNode) map.get("currentNode");
            approvalRecord.setFlowNodeId(currentNode.getId());
            approvalService.pass(approvalRecord, currentNode.getNextId());
        }
        //如果是最后一个节点，工单结束
        else {
            approvalService.finish(approvalRecord);
        }

        return Result.ok();
    }


}
