package cn.edu.bistu.approval;

import cn.edu.bistu.model.entity.ApprovalRecord;
import cn.edu.bistu.model.vo.WorkOrderVo;



/**
 * 工单流转器，负责在审批者/审批系统进行审批操作后将工单流转到下一个节点
 * @author wc
 */
public interface WorkOrderFlower {

    /**
     * 流转工单到下一个节点，一般来说，流转应该包含以下几个逻辑：
     *              判断工单是否处于最后一个节点，
     *                  若是，则委托 * WorkOrderFinisher * 将工单结束
     *                  否则，将工单流转到下一个节点：
     *                      保存工单审批记录 approvalRecord
     *                      委托 ActualApproverFinalizer 动态设置下一个审批节点的真实审批者id
     *                      更新工单的其他状态，比如是否被审批、工单的审批节点
     *                  若下一个节点的审批者是一个其他系统提供的指令，则执行该指令，根据指令执行的结果判断是否需要往下递归流转
     *
     * @param workOrderVo 流转前的 full prepared 的工单
     * @param approvalRecord full prepared 的工单审批记录
     */
    public void flow(WorkOrderVo workOrderVo, ApprovalRecord approvalRecord);
}
