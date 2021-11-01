package cn.edu.bistu.workOrder.service;

import cn.edu.bistu.admin.User.mapper.UserDao;
import cn.edu.bistu.approval.dao.ApproverLogicDao;
import cn.edu.bistu.model.entity.ApproverLogic;
import cn.edu.bistu.model.entity.FlowNodeApprover;
import cn.edu.bistu.model.vo.FlowNodeVo;
import cn.edu.bistu.model.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class LogicApproverFinder implements FlowNodeApproverDecider{


    @Autowired
    ApproverLogicDao approverLogicDao;

    @Override
    public String getType() {
        return "logicType";
    }

    @Override
    public FlowNodeApprover findAndSetFlowNodeApprover(FlowNodeVo flowNode) {
        ApproverLogic approverLogicOfResultWorkOrderFlowNode = (ApproverLogic)approverLogicDao.getApproverLogicByLogicId(flowNode.getApproverId() * -1).getResult();
        flowNode.setFlowNodeApprover(approverLogicOfResultWorkOrderFlowNode);
        return approverLogicOfResultWorkOrderFlowNode;
    }
}
