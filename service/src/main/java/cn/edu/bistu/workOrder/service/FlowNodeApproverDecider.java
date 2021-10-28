package cn.edu.bistu.workOrder.service;

import cn.edu.bistu.model.entity.FlowNode;
import cn.edu.bistu.model.entity.FlowNodeApprover;
import cn.edu.bistu.model.vo.FlowNodeVo;
import cn.edu.bistu.model.vo.FlowVo;

public interface FlowNodeApproverDecider {

    String getType();

    FlowNodeApprover findAndSetFlowNodeApprover(FlowNodeVo flowNode);

}
