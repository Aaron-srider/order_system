package cn.edu.bistu.approval;

import cn.edu.bistu.admin.User.mapper.UserDao;
import cn.edu.bistu.approval.mapper.ApprovalRecordMapper;
import cn.edu.bistu.common.exception.ResultCodeException;
import cn.edu.bistu.constants.ApprovalOperation;
import cn.edu.bistu.constants.ResultCodeEnum;
import cn.edu.bistu.flow.dao.FlowDao;
import cn.edu.bistu.model.entity.ApprovalRecord;
import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.model.entity.auth.User;
import cn.edu.bistu.model.vo.UserVo;
import cn.edu.bistu.model.vo.WorkOrderVo;
import cn.edu.bistu.workOrder.dao.WorkOrderDao;
import cn.edu.bistu.workOrder.service.ActualApproverFinalizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class WorkOrderFlowerImpl implements WorkOrderFlower {

    @Autowired
    FlowDao flowDao;

    @Autowired
    UserDao userDao;

    @Qualifier("workOrderDaoImpl")
    @Autowired
    WorkOrderDao workOrderDao;

    @Autowired
    ApprovalRecordMapper approvalRecordMapper;

    @Autowired
    WorkOrderFinisherFactory workOrderFinisherFactory;

    @Autowired
    ActualApproverFinalizer actualApproverFinalizer;

    @Override
    public void flow(WorkOrderVo workOrderVo, ApprovalRecord approvalRecord) {

        //查看工单下一个审批节点，没有下一个节点时返回null
        Long nextFlowNodeId = workOrderVo.getFlowNode().getNextId();

        //工单处于最后一个审批节点，工单结束
        if (nextFlowNodeId == null) {
            WorkOrderFinishWrapper workOrderFinishWrapper = new WorkOrderFinishWrapper();
            workOrderFinishWrapper.setApprovalRecord(approvalRecord);
            workOrderFinishWrapper.setFullPreparedWorkOrderToBeFinished(workOrderVo);
            workOrderFinisherFactory
                    .getFinisher("approvalTypeV2")
                    .finishWorkOrder(workOrderFinishWrapper);
        }
        //工单流转
        else {
            //保存审批记录
            approvalRecordMapper.insert(approvalRecord);

            //动态决定工单的实际审批者
            actualApproverFinalizer.decideActualApprover(workOrderVo, true);

            WorkOrder workOrder1 = new WorkOrder();

            //更新工单
            workOrder1.setId(workOrderVo.getId());
            workOrder1.setStatus(0);
            workOrder1.setIsExamined(1);
            workOrder1.setFlowNodeId(nextFlowNodeId);
            workOrder1.setActualApproverId(workOrderVo.getActualApproverId());

            workOrderDao.updateById(workOrder1);

            WorkOrderVo workOrderVoAfterUpdate = workOrderDao.getOneWorkOrderById(workOrderVo.getId()).getResult();

            if(workOrderVoAfterUpdate.getActualApproverId() < 0) {
                //    为工单发起者（研究生）添加导师
                UserVo initiator = userDao.getOneUserById(workOrderVoAfterUpdate.getInitiatorId()).getResult();
                User user = new User();
                user.setId(initiator.getId());
                if(workOrderVoAfterUpdate.getUserSpecifiedId() == null) {
                    throw new ResultCodeException("",
                            ResultCodeEnum.USER_SPECIFIED_ID_NULL);
                }
                user.setTutorId(workOrderVoAfterUpdate.getUserSpecifiedId());
                userDao.simpleUpdateUserById(user);

                ApprovalRecord approvalRecord1 = new ApprovalRecord();
                approvalRecord1.setOperation(ApprovalOperation.PASS.getCode());
                approvalRecord1.setApproverId(workOrderVoAfterUpdate.getActualApproverId());
                approvalRecord1.setFlowNodeId(workOrderVoAfterUpdate.getFlowNodeId());
                approvalRecord1.setComment("导师已更换");
                approvalRecord1.setWorkOrderId(workOrderVoAfterUpdate.getId());
                flow(workOrderVoAfterUpdate, approvalRecord1);
            }

        }
    }
}
