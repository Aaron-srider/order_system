package cn.edu.bistu.approval.service;

import cn.edu.bistu.model.common.ServiceResult;
import cn.edu.bistu.model.entity.ApprovalRecord;
import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.model.vo.WorkOrderVo;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.Map;

public interface ApprovalService {

    /**
     * 工单审批通过逻辑，若工单处于最后一个节点，触发工单结束逻辑；否则，触发工单流转逻辑。
     * @param approvalRecord 审批记录
     */
    void pass(ApprovalRecord approvalRecord) throws NoSuchFieldException, IllegalAccessException;

    void reject(ApprovalRecord approvalRecord) throws NoSuchFieldException, IllegalAccessException;

    ServiceResult<Page<JSONObject>> listWorkOrderToBeApproved(Long visitorId, WorkOrderVo workOrderVo) throws NoSuchFieldException, IllegalAccessException;
}
