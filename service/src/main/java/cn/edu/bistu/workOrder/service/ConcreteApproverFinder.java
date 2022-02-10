package cn.edu.bistu.workOrder.service;

import cn.edu.bistu.admin.user.dao.UserDao;
import cn.edu.bistu.model.entity.FlowNodeApprover;
import cn.edu.bistu.model.vo.FlowNodeVo;
import cn.edu.bistu.model.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConcreteApproverFinder implements FlowNodeApproverDecider{

    @Autowired
    UserDao userDao;

    @Override
    public String getType() {
        return "concreteType";
    }

    @Override
    public FlowNodeApprover findAndSetFlowNodeApprover(FlowNodeVo flowNode) {
        UserVo concreteApproverOfResultWorkOrderFlowNode = userDao.getOneUserById(flowNode.getApproverId()).getResult();
        flowNode.setFlowNodeApprover(concreteApproverOfResultWorkOrderFlowNode);
        return concreteApproverOfResultWorkOrderFlowNode;
    }
}
