package cn.edu.bistu.approval.service;

import cn.edu.bistu.approval.WorkOrderFinisher;
import cn.edu.bistu.constants.ApprovalOperationEnum;
import cn.edu.bistu.constants.WorkOrderStatus;
import cn.edu.bistu.model.common.result.ServiceResult;
import cn.edu.bistu.model.entity.ApprovalRecord;
import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.model.vo.WorkOrderVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @author wc
 */
public interface ApprovalService {
    /**
     * 当工单结束时，调用该方法，比如工单因为审批不通过，或用户撤回，都导致该方法被调用。
     * @param workOrderFinisher 工单结束器具体实现类
     * @param workOrder 待结束的工单，待完善数据：
     *                  status:工单状态待更新
     *                  isFinished:工单是否结束
     * @param approvalRecord 审批记录,待完善数据域:
     *                       flowNodeId:审批的节点id
     *                       approvalOperation:审批的操作，如同意，不同意
     * @param finishStatus 工单结束时的状态（REJECT或PASS或revoke或invalidation）
     * @param approvalOperation 审批操作
     */
    public void workOrderFinish(WorkOrderFinisher workOrderFinisher, WorkOrder workOrder, ApprovalRecord approvalRecord, WorkOrderStatus finishStatus,
                                ApprovalOperationEnum approvalOperation);

    /**
     * 工单审批通过逻辑，若工单处于最后一个节点，触发工单结束逻辑；否则，触发工单流转逻辑。
     * @param approvalRecord 审批记录，已经包含前端传来数据：
     *                       work_order_id：审批的工单Id,
     *                       comment：审批留言,
     *                       approver_id：审批者（该信息从token中获取）
     *                       缺少信息：
     *                       operation：审批操作，
     *                       flow_node_id：审批节点
     */
    void pass(ApprovalRecord approvalRecord);


    /**
     * 工单审批通过逻辑，若工单处于最后一个节点，触发工单结束逻辑；否则，触发工单流转逻辑。
     * @param approvalRecord 审批记录，已经包含前端传来数据：
     *                       work_order_id：审批的工单Id,
     *                       comment：审批留言,
     *                       approver_id：审批者（该信息从token中获取）
     *                       缺少信息：
     *                       operation：审批操作，
     *                       flow_node_id：审批节点
     */
    void reject(ApprovalRecord approvalRecord);


    ServiceResult<Page<WorkOrderVo>> listWorkOrderToBeApproved(Long visitorId, Page<WorkOrderVo> page, WorkOrderVo workOrderVo) ;
}
