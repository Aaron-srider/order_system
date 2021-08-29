package cn.edu.bistu.approval.service;

import cn.edu.bistu.approval.mapper.ApprovalRecordMapper;
import cn.edu.bistu.common.BeanUtils;
import cn.edu.bistu.flow.service.FlowNodeService;
import cn.edu.bistu.model.entity.ApprovalRecord;
import cn.edu.bistu.model.entity.FlowNode;
import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.model.entity.WorkOrderHistory;
import cn.edu.bistu.workOrder.service.WorkOrderHistoryService;
import cn.edu.bistu.workOrder.service.WorkOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Slf4j
@Service
public class ApprovalServiceImpl implements ApprovalService {

    @Autowired
    WorkOrderService workOrderService;

    @Autowired
    FlowNodeService flowNodeService;

    @Autowired
    ApprovalRecordMapper approvalRecordMapper;

    @Autowired
    WorkOrderHistoryService workOrderHistoryService;

    /**
     * 检查指定工单是否到达最后一个节点
     * @param workOrderId 工单id
     * @return 结果Map：isLastNode->boolean;nextNode->FlowNode
     */
    public  Map<String, Object>  isLastNode(Long workOrderId) {
        WorkOrder workOrder = workOrderService.getById(workOrderId);

        Long flowId = workOrder.getFlowId();

        List<FlowNode> flowNodeList = flowNodeService.getFlowNodeByFlowId(flowId);

        System.out.println(flowNodeList);

        Long currentFlowNodeId = workOrder.getFlowNodeId();

        boolean isLastNode = flowNodeList.get(flowNodeList.size() - 1).getId().equals(currentFlowNodeId);
        Map<String, Object> map = new HashMap<>();
        FlowNode nextNode = null;
        FlowNode currentNode = null;
        if(!isLastNode) {

            for (int i = 0; i < flowNodeList.size(); i++) {

                FlowNode flowNode  = flowNodeList.get(i);
                if(flowNode.getId().equals(currentFlowNodeId)) {
                    currentNode = flowNodeList.get(i);
                    nextNode = flowNodeList.get(i + 1);
                    break;
                }
            }

            map.put("isLastNode", false);
            map.put("nextNode", nextNode);
            map.put("currentNode", currentNode);
        } else {
            map.put("isLastNode", true);
            map.put("nextNode", null);
            map.put("currentNode", currentNode);
        }

        return map;
    }


    @Override
    public void pass(ApprovalRecord approvalRecord, Long nextFlowId) {

        //保存审批记录
        approvalRecord.setApprovalDatetime(new Date());
        approvalRecord.setOperation(0);
        approvalRecordMapper.insert(approvalRecord);

        //更新工单的flow_node_id字段
        WorkOrder workOrder = new WorkOrder();
        workOrder.setId(approvalRecord.getWorkOrderId());
        workOrder.setFlowNodeId(nextFlowId);
        workOrderService.updateById(workOrder);

    }


    @Override
    public void finish(ApprovalRecord approvalRecord) {

        //更新工单状态
        WorkOrder workOrder = workOrderService.getById(approvalRecord.getWorkOrderId());

        approvalRecord.setFlowNodeId(workOrder.getFlowNodeId());

        workOrder.setStatus(1);
        workOrder.setIsFinished(1);
        workOrder.setIsExamined(1);

        workOrderService.updateById(workOrder);

        log.debug("workOrder to be updated:" + workOrder);


        //生成历史工单
        WorkOrderHistory workOrderHistory = new WorkOrderHistory();

        BeanUtils.copyProperties(workOrder, workOrderHistory);

        log.debug("workOrderHistory to be saved:" + workOrderHistory);

        workOrderHistoryService.save(workOrderHistory);


        //保存审批记录
        approvalRecord.setOperation(0);
        approvalRecord.setApprovalDatetime(new Date());

        approvalRecordMapper.insert(approvalRecord);

    }

    @Override
    public void reject(ApprovalRecord approvalRecord) {

        //更新工单状态
        WorkOrder workOrder = workOrderService.getById(approvalRecord.getWorkOrderId());

        approvalRecord.setFlowNodeId(workOrder.getFlowNodeId());

        workOrder.setStatus(2);
        workOrder.setIsFinished(1);
        workOrder.setIsExamined(1);

        workOrderService.updateById(workOrder);

        log.debug("workOrder to be updated:" + workOrder);


        //生成历史工单
        WorkOrderHistory workOrderHistory = new WorkOrderHistory();

        BeanUtils.copyProperties(workOrder, workOrderHistory);

        log.debug("workOrderHistory to be saved:" + workOrderHistory);

        workOrderHistoryService.save(workOrderHistory);


        //保存审批记录
        approvalRecord.setOperation(1);
        approvalRecord.setApprovalDatetime(new Date());

        approvalRecordMapper.insert(approvalRecord);
    }

}
