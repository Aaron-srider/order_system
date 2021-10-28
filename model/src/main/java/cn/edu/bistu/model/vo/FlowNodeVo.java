package cn.edu.bistu.model.vo;

import cn.edu.bistu.model.entity.ApproverLogic;
import cn.edu.bistu.model.entity.FlowNode;
import cn.edu.bistu.model.entity.FlowNodeApprover;
import lombok.Data;

@Data
public class FlowNodeVo extends FlowNode {

    /**
     * 将节点的审批者信息返回给前端
     */
    private UserVo approver;

    /**
     * 将节点的审批逻辑信息返回给前端
     */
    private ApproverLogic approverLogic;


    /**
     * 将节点的审批者信息返回给前端
     */
    private FlowNodeApprover flowNodeApprover;

}
