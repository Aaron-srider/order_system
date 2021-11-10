package cn.edu.bistu.approval;

import cn.edu.bistu.user.dao.UserDao;
import cn.edu.bistu.approval.dao.ApproverLogicDao;
import cn.edu.bistu.approval.mapper.ApprovalRecordMapper;
import cn.edu.bistu.constants.ApprovalOperation;
import cn.edu.bistu.constants.WorkOrderStatus;
import cn.edu.bistu.externalApi.ExternalApiImplementationFactory;
import cn.edu.bistu.externalApi.ExternalApiResult;
import cn.edu.bistu.flow.dao.FlowDao;
import cn.edu.bistu.model.entity.ApprovalRecord;
import cn.edu.bistu.model.entity.ApproverLogic;
import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.model.vo.FlowVo;
import cn.edu.bistu.model.vo.WorkOrderVo;
import cn.edu.bistu.workOrder.dao.WorkOrderDao;
import cn.edu.bistu.workOrder.service.ActualApproverFinalizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class WorkOrderFlowerImpl implements WorkOrderFlower {


    @Autowired
    ApproverLogicDao approverLogicDao;

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

    @Autowired
    ExternalApiImplementationFactory externalApiImplementationFactory;


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

            FlowVo fullPreparedFlow = flowDao.getFullPreparedFlowByFlowId(workOrderVoAfterUpdate.getFlowId()).getResult();

            Long actualApproverId = workOrderVoAfterUpdate.getActualApproverId();
            if (actualApproverId < 0) {

                ApproverLogic approverLogic = (ApproverLogic) approverLogicDao.getApproverLogicByLogicId(actualApproverId * -1).getResult();
                ExternalApiResult externalApiResult = externalApiImplementationFactory.getImplementation(approverLogic.getText())
                        .execute(
                                workOrderVoAfterUpdate,
                                fullPreparedFlow
                        );

                ApprovalOperation externalApiExcuteOperation = externalApiResult.getWorkOrderStatusOfExcution();

                if (externalApiExcuteOperation.equals(ApprovalOperation.PASS)) {
                    WorkOrderVo workOrderVoAfterUpdate2 = workOrderDao.getOneWorkOrderById(workOrderVo.getId()).getResult();

                    ApprovalRecord approvalRecord1 = new ApprovalRecord();
                    approvalRecord1.setOperation(ApprovalOperation.PASS.getCode());
                    approvalRecord1.setApproverId(workOrderVoAfterUpdate2.getActualApproverId());
                    approvalRecord1.setFlowNodeId(workOrderVoAfterUpdate2.getFlowNodeId());
                    approvalRecord1.setComment(approverLogic.getName() + "已经处理完毕，工单通过");
                    approvalRecord1.setWorkOrderId(workOrderVoAfterUpdate2.getId());

                    flow(workOrderVoAfterUpdate2, approvalRecord1);
                } else if (externalApiExcuteOperation.equals(ApprovalOperation.REJECT)) {
                    WorkOrderVo workOrderVoAfterUpdate2 = workOrderDao.getOneWorkOrderById(workOrderVo.getId()).getResult();

                    ApprovalRecord approvalRecord1 = new ApprovalRecord();
                    approvalRecord1.setOperation(ApprovalOperation.REJECT.getCode());
                    approvalRecord1.setApproverId(workOrderVoAfterUpdate2.getActualApproverId());
                    approvalRecord1.setFlowNodeId(workOrderVoAfterUpdate2.getFlowNodeId());
                    approvalRecord1.setComment(approverLogic.getName() + "已经处理完毕，工单不通过");
                    approvalRecord1.setWorkOrderId(workOrderVoAfterUpdate2.getId());

                    WorkOrderFinishWrapper workOrderFinishWrapper = new WorkOrderFinishWrapper();
                    workOrderFinishWrapper.setFinishStatusConstant(WorkOrderStatus.NOT_APPROVED);
                    workOrderFinishWrapper.setFullPreparedWorkOrderToBeFinished(workOrderVoAfterUpdate2);
                    workOrderFinishWrapper.setApprovalRecord(approvalRecord1);

                    workOrderFinisherFactory.getFinisher("approvalTypeV2")
                            .finishWorkOrder(workOrderFinishWrapper);
                }
            }

        }

    }
}
