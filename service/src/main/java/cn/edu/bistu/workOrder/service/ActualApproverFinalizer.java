package cn.edu.bistu.workOrder.service;

import cn.edu.bistu.user.dao.UserDao;
import cn.edu.bistu.approval.WorkOrderFinisherFactory;
import cn.edu.bistu.approval.WorkOrderFlower;
import cn.edu.bistu.approval.dao.ApproverLogicDao;
import cn.edu.bistu.common.exception.ResultCodeException;
import cn.edu.bistu.constants.ResultCodeEnum;
import cn.edu.bistu.flow.dao.FlowDao;
import cn.edu.bistu.model.entity.ApproverLogic;
import cn.edu.bistu.model.entity.FlowNode;
import cn.edu.bistu.model.vo.UserVo;
import cn.edu.bistu.model.vo.WorkOrderVo;
import cn.edu.bistu.workOrder.dao.WorkOrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * 在工单流转的过程中动态决定工单的审批者，并存储在工单中，具体作用请看decideActualApprover()的文档
 * @author wc
 */
@Component
public class ActualApproverFinalizer {

    @Autowired
    WorkOrderFlower workOrderFlower;

    @Autowired
    ApproverLogicDao approverLogicDao;

    @Autowired
    UserDao userDao;

    @Autowired
    FlowDao flowDao;

    @Qualifier("workOrderDaoImpl")
    @Autowired
    WorkOrderDao workOrderDao;

    @Autowired
    WorkOrderFinisherFactory workOrderFinisherFactory;

    /**
     * 动态决定工单的真实审批者，如果节点审批者是逻辑，将逻辑转换成真实用户的id更新到工单中；如果审批者是其他系统，
     * 则直接将逻辑表中该系统对应的id的负数设置为工单的真实审批id；查询工单的真实审批者时，如果检查到id是负数，则判定
     * 审批者是其他系统。
     * 决定因素：
     * 工单所处节点的审批者id
     * 具体决定方式：
     * 如果节点审批者id为正，将节点审批者id作为实际审批者id；如果为负，则取反查询审批者决定逻辑表，找到对应的审批者id。
     *
     * @param workOrderVo 工单流转前的 full prepared 的工单
     */
    public void decideActualApprover(WorkOrderVo workOrderVo, boolean workOrderStart) {

        FlowNode flowNode = workOrderVo.getFlowNode();
        Long approverId = null;
        //处理发起工单时的情况
        if (!workOrderStart) {
            approverId = ((FlowNode) flowDao.getOneFlowNodeByNodeId(workOrderVo.getFlowNodeId()).getResult()).getApproverId();
        } else {
            approverId = ((FlowNode) flowDao.getOneFlowNodeByNodeId(workOrderVo.getFlowNode().getNextId()).getResult()).getApproverId();
        }

        //确定工单的实际审批者
        if (approverId > 0) {
            //    字段表示用户表中的id
            workOrderVo.setActualApproverId(approverId);

        } else if (approverId < 0) {
            //    字段表示逻辑表中id的相反数
            Long logicId = approverId * -1;

            ApproverLogic approverLogic = (ApproverLogic) approverLogicDao.getApproverLogicByLogicId(logicId).getResult();

            if (approverLogic.getText().equals("TUTOR_APPROVAL")) {
                UserVo initiator = userDao.getOneUserById(workOrderVo.getInitiatorId()).getResult();
                if (initiator.getTutorId() == null) {
                    throw new ResultCodeException("user id: " + initiator.getId(),
                            ResultCodeEnum.HAVE_NOT_TUTOR_YET);
                }

                workOrderVo.setActualApproverId(initiator.getTutorId());
            } else if (approverLogic.getText().equals("STUDENT_SPECIFY")) {
                if (workOrderVo.getUserSpecifiedId() == null) {
                    throw new ResultCodeException("",
                            ResultCodeEnum.USER_SPECIFIED_ID_NULL);
                }
                workOrderVo.setActualApproverId(workOrderVo.getUserSpecifiedId());
            } else{

                workOrderVo.setActualApproverId(approverId);

            }
        }


    }
}
