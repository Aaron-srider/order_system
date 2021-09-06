package cn.edu.bistu.approval.service;

import cn.edu.bistu.model.entity.ApprovalRecord;
import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.model.vo.WorkOrderVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface ApprovalService {

    /**
     * 检查指定工单是否到达最后一个节点
     * @param workOrderId 工单id
     * @return 结果Map：isLastNode->boolean;nextNode->FlowNode;currentFNode->FlowNode。
     */
    Map<String, Object> isLastNode(Long workOrderId) ;

    /**
     * 工单审批通过逻辑，若工单处于最后一个节点，触发工单结束逻辑；否则，触发工单流转逻辑。
     * @param approvalRecord 审批记录
     */
    void pass(ApprovalRecord approvalRecord);

    void reject(ApprovalRecord approvalRecord);

    /**
     * 工单流转逻辑，将工单流转到下一个节点，并保存审批记录，该方法的触发条件是审批通过或提交工单。
     * @param workOrder 待流转工单，待完善信息：工单状态，是否被审批过，工单下一个审批节点。
     * @param approvalRecord 造成工单结束的审批记录，待完善信息：审批操作，审批节点id，审批时间。
     * @param nextFlowNodeId 下一个审批节点id
     */
    void WorkOrderFlowToNext(WorkOrder workOrder, ApprovalRecord approvalRecord, Long nextFlowNodeId);

    /**
     * 工单结束逻辑，更新工单状态并保存，生成历史工单并保存，保存审批记录，将工单结束状态通过微信发送给工单发起者。
     * @param workOrder 待结束的工单，待完善信息：工单状态，工单是否结束，工单是否被审批。
     * @param approvalRecord 造成工单结束的审批记录，待完善信息：审批操作，审批节点id，审批时间。
     */
    void workOrderFinish(WorkOrder workOrder, ApprovalRecord approvalRecord);

    Page<WorkOrderVo> listWorkOrderToBeApproved(Long visitorId, Map<String, Object> page);
}
