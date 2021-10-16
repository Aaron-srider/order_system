package cn.edu.bistu.approval.service;

import cn.edu.bistu.constants.WorkOrderStatus;
import cn.edu.bistu.model.common.result.ServiceResult;
import cn.edu.bistu.model.entity.ApprovalRecord;
import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.model.vo.WorkOrderVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface ApprovalService {

    public void workOrderFinish(WorkOrder workOrder, ApprovalRecord approvalRecord, WorkOrderStatus finishStatus);

    /**
     * 工单审批通过逻辑，若工单处于最后一个节点，触发工单结束逻辑；否则，触发工单流转逻辑。
     * @param approvalRecord 审批记录
     */
    void pass(ApprovalRecord approvalRecord) throws NoSuchFieldException, IllegalAccessException;

    void reject(ApprovalRecord approvalRecord) throws NoSuchFieldException, IllegalAccessException;

    ServiceResult<Page<WorkOrderVo>> listWorkOrderToBeApproved(Long visitorId, Page<WorkOrderVo> page, WorkOrderVo workOrderVo) throws NoSuchFieldException, IllegalAccessException;
}
