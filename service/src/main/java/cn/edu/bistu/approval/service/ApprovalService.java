package cn.edu.bistu.approval.service;

import cn.edu.bistu.model.entity.ApprovalRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface ApprovalService {

    /**
     * 检查指定工单是否到达最后一个节点
     * @param workOrderId 工单id
     * @return 结果Map：isLastNode->boolean;nextNode->FlowNode
     */
    Map<String, Object> isLastNode(Long workOrderId) ;

    void pass(ApprovalRecord approvalRecord, Long nextFlowId);

    void finish(ApprovalRecord approvalRecord);
}
