package cn.edu.bistu.approval.service;

import cn.edu.bistu.approval.mapper.ApprovalRecordMapper;
import cn.edu.bistu.auth.mapper.UserMapper;
import cn.edu.bistu.common.BeanUtils;
import cn.edu.bistu.common.exception.WorkOrderBeenFinishedException;
import cn.edu.bistu.constants.ResultCodeEnum;
import cn.edu.bistu.flow.service.FlowNodeService;
import cn.edu.bistu.model.entity.ApprovalRecord;
import cn.edu.bistu.model.entity.FlowNode;
import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.model.entity.WorkOrderHistory;
import cn.edu.bistu.model.vo.UserVo;
import cn.edu.bistu.workOrder.service.WorkOrderHistoryService;
import cn.edu.bistu.workOrder.service.WorkOrderService;
import cn.edu.bistu.wx.service.WxMiniApi;
import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
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

    @Autowired
    UserMapper userMapper;

    @Autowired
    WxMiniApi wxMiniApi;


    /**
     * 检查指定工单是否到达最后一个节点
     *
     * @param workOrderId 工单id
     * @return 结果Map：isLastNode->boolean;nextNode->FlowNode
     */
    public Map<String, Object> isLastNode(Long workOrderId) {
        WorkOrder workOrder = workOrderService.getById(workOrderId);

        Long flowId = workOrder.getFlowId();

        List<FlowNode> flowNodeList = flowNodeService.getFlowNodeByFlowId(flowId);

        System.out.println(flowNodeList);

        Long currentFlowNodeId = workOrder.getFlowNodeId();

        boolean isLastNode = flowNodeList.get(flowNodeList.size() - 1).getId().equals(currentFlowNodeId);
        Map<String, Object> map = new HashMap<>();
        FlowNode nextNode = null;
        FlowNode currentNode = null;
        if (!isLastNode) {

            for (int i = 0; i < flowNodeList.size(); i++) {

                FlowNode flowNode = flowNodeList.get(i);
                if (flowNode.getId().equals(currentFlowNodeId)) {
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


    /**
     * 工单审批通过逻辑，若工单处于最后一个节点，触发工单结束逻辑；否则，触发工单流转逻辑。
     * @param approvalRecord 审批记录
     */
    @Override
    public void pass(ApprovalRecord approvalRecord) {
        Long workOrderId = approvalRecord.getWorkOrderId();
        WorkOrder workOrder = workOrderService.getById(workOrderId);

        //若工单已结束，审批操作非法
        if (workOrder.isFinished()) {
            throw new WorkOrderBeenFinishedException("workOrderId: " + workOrderId,
                    ResultCodeEnum.WORKORDER_BEEN_FINISHED);
        }

        //判断工单是否处于最后一个审批节点
        final Map<String, Object> lastNodeMap = isLastNode(workOrderId);
        boolean isLastNode = (boolean) lastNodeMap.get("isLastNode");

        //工单处于最后一个审批节点，工单结束
        if (isLastNode) {
            workOrderFinish(workOrder, approvalRecord);
        }
        //工单流转
        else {
            Long nextFlowNodeId = ((FlowNode)lastNodeMap.get("nextNode")).getId();
            WorkOrderFlowToNext(workOrder, approvalRecord, nextFlowNodeId);
        }
    }

    /**
     * 工单流转逻辑，将工单流转到下一个节点，并保存审批记录，该方法的触发条件是审批通过或提交工单。
     * @param workOrder 待流转工单，待完善信息：工单状态，是否被审批过，工单下一个审批节点。
     * @param approvalRecord 造成工单结束的审批记录，待完善信息：审批操作，审批节点id，审批时间。
     * @param nextFlowNodeId 下一个审批节点id
     */
    @Override
    public void WorkOrderFlowToNext(WorkOrder workOrder, ApprovalRecord approvalRecord, Long nextFlowNodeId) {

        //保存审批记录
        approvalRecord.setFlowNodeId(workOrder.getFlowNodeId());
        approvalRecord.setOperation(0);
        approvalRecord.setApprovalDatetime(new Date());
        approvalRecordMapper.insert(approvalRecord);

        //更新工单
        workOrder.setStatus(0);     //在审
        workOrder.setIsExamined(1);  //已经被审批过
        workOrder.setFlowNodeId(nextFlowNodeId);    //更新工单审批节点
        workOrderService.updateById(workOrder);
    }


    /**
     * 工单结束逻辑，更新工单状态并保存，生成历史工单并保存，保存审批记录，将工单结束状态通过微信发送给工单发起者。
     * @param workOrder 待结束的工单，待完善信息：工单状态，工单是否结束，工单是否被审批。
     * @param approvalRecord 造成工单结束的审批记录，待完善信息：审批操作，审批节点id，审批时间。
     */
    @Override
    public void workOrderFinish(WorkOrder workOrder, ApprovalRecord approvalRecord) {

        //保存审批记录
        approvalRecord.setFlowNodeId(workOrder.getFlowNodeId());    //这一步一定要在更新工单状态之前
        approvalRecord.setOperation(0);
        approvalRecord.setApprovalDatetime(new Date());
        approvalRecordMapper.insert(approvalRecord);

        //更新工单状态
        workOrder.setStatus(1);         //顺利结束
        workOrder.setIsFinished(1);     //工单结束
        workOrder.setIsExamined(1);     //工单已经被审批过
        workOrderService.updateById(workOrder);
        log.debug("workOrder to be updated:" + workOrder);

        //生成历史工单
        WorkOrderHistory workOrderHistory = new WorkOrderHistory();
        BeanUtils.copyProperties(workOrder, workOrderHistory);
        workOrderHistory.setWorkOrderId(workOrder.getId());
        log.debug("workOrderHistory to be saved:" + workOrderHistory);
        workOrderHistoryService.save(workOrderHistory);

        //发送微信通知
        Long initiatorId = workOrder.getInitiatorId();
        UserVo userVo = userMapper.selectById(initiatorId);
        String openId = userVo.getOpenId();
        //模板还没选好，此步跳过
        //wxMiniApi.sendSubscribeMsg(openId);

    }

    @Override
    public void reject(ApprovalRecord approvalRecord) {

        //检查工单是否已经结束
        WorkOrder workOrder = workOrderService.getById(approvalRecord.getWorkOrderId());
        if (workOrder.getIsFinished().equals(1)) {
            throw new
                    WorkOrderBeenFinishedException(
                    "workOrderId: " + workOrder.getId(),
                    ResultCodeEnum.WORKORDER_BEEN_FINISHED
            );
        }

        //保存审批记录
        approvalRecord.setFlowNodeId(workOrder.getFlowNodeId());    //这一步一定要在更新工单状态之前
        approvalRecord.setOperation(1);
        approvalRecord.setApprovalDatetime(new Date());
        approvalRecordMapper.insert(approvalRecord);



        workOrder.setStatus(2);
        workOrder.setIsFinished(1);
        workOrder.setIsExamined(1);

        workOrderService.updateById(workOrder);

        log.debug("workOrder to be updated:" + workOrder);


        //生成历史工单
        WorkOrderHistory workOrderHistory = new WorkOrderHistory();

        BeanUtils.copyProperties(workOrder, workOrderHistory);

        workOrderHistory.setWorkOrderId(workOrder.getId());

        log.debug("workOrderHistory to be saved:" + workOrderHistory);

        workOrderHistoryService.save(workOrderHistory);


        //发送微信通知
        Long initiatorId = workOrder.getInitiatorId();

        UserVo userVo = userMapper.selectById(initiatorId);
        String openId = userVo.getOpenId();
        wxMiniApi.sendSubscribeMsg(openId);

    }

}
