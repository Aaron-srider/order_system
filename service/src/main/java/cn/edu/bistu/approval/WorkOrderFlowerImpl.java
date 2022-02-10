package cn.edu.bistu.approval;

import cn.edu.bistu.admin.user.dao.UserDao;
import cn.edu.bistu.approval.dao.ApproverLogicDao;
import cn.edu.bistu.approval.mapper.ApprovalRecordMapper;
import cn.edu.bistu.constants.ApprovalOperationEnum;
import cn.edu.bistu.constants.WorkOrderStatus;
import cn.edu.bistu.externalApi.ExternalApiImplementationFactory;
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

import java.util.Date;

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


    private WorkOrderStatus judgeWorkOrderStatus(Integer approvalOper) {

        WorkOrderStatus workOrderStatus = null;
        if (approvalOper.equals(ApprovalOperationEnum.PASS.getCode())) {
            workOrderStatus = WorkOrderStatus.COMPLETED_SUCCESSFULLY;
        } else {
            workOrderStatus = WorkOrderStatus.NOT_APPROVED;
        }
        return workOrderStatus;
    }

    private void processLogicApprover(Long actualApproverId, Long workOrderId) {
        WorkOrderVo workOrderVoAfterFlow = workOrderDao.getOneWorkOrderById(workOrderId).getResult();
        FlowVo fullPreparedFlow = flowDao.getFullPreparedFlowByFlowId(workOrderVoAfterFlow.getFlowId()).getResult();

        //根据逻辑号获取第三方审批逻辑
        ApproverLogic approveLogic = (ApproverLogic) approverLogicDao.getApproverLogicByLogicId(actualApproverId * -1).getResult();

        //执行审批逻辑并获得结果
        ApprovalOperationEnum externalApiExcuteOperation = externalApiImplementationFactory.getImplementation(approveLogic.getText())
                .execute(
                        workOrderVoAfterFlow,
                        fullPreparedFlow
                ).getWorkOrderStatusOfExcution();

        //根据审批结果判断审批后工单去向
        WorkOrderVo workOrderVoAfterUpdate2 = workOrderDao.getOneWorkOrderById(workOrderId).getResult();

        if (externalApiExcuteOperation.equals(ApprovalOperationEnum.PASS)) { //审批通过

            ApprovalRecord approvalRecord = ApprovalRecord.getFullInstance(
                    ApprovalOperationEnum.PASS.getCode(),
                    new Date().toString(),
                    approveLogic.getName() + "已经处理完毕，工单通过",
                    workOrderVoAfterUpdate2.getActualApproverId(),
                    workOrderVoAfterUpdate2.getFlowNodeId(),
                    workOrderVoAfterUpdate2.getId()
            );

            flow(workOrderVoAfterUpdate2, approvalRecord);
        } else if (externalApiExcuteOperation.equals(ApprovalOperationEnum.REJECT)) { //审批不通过

            ApprovalRecord approvalRecord = ApprovalRecord.getFullInstance(
                    ApprovalOperationEnum.REJECT.getCode(),
                    new Date().toString(),
                    approveLogic.getName() + "已经处理完毕，工单不通过",
                    workOrderVoAfterUpdate2.getActualApproverId(),
                    workOrderVoAfterUpdate2.getFlowNodeId(),
                    workOrderVoAfterUpdate2.getId()
            );

            //结束工单
            WorkOrderFinishContext workOrderFinishContext = WorkOrderFinishContext.getFullInstance(
                    workOrderVoAfterUpdate2,
                    approvalRecord,
                    WorkOrderStatus.NOT_APPROVED
            );
            workOrderFinisherFactory
                    .getFinisher("approvalTypeV2")
                    .finishWorkOrder(workOrderFinishContext);
        }
    }

    @Override
    public void flow(WorkOrderVo workOrderVo, ApprovalRecord approvalRecord) {

        //查看工单下一个审批节点，没有下一个节点时返回null
        Long nextFlowNodeId = workOrderVo.getFlowNode().getNextId();

        //工单处于最后一个审批节点，工单结束
        if (nextFlowNodeId == null) {
            WorkOrderFinishContext workOrderFinishContext = WorkOrderFinishContext.getFullInstance(
                    workOrderVo,
                    approvalRecord,
                    judgeWorkOrderStatus(approvalRecord.getOperation())
            );
            workOrderFinisherFactory
                    .getFinisher("approvalTypeV2")
                    .finishWorkOrder(workOrderFinishContext);
        }
        //工单流转
        else {
            //保存审批记录
            approvalRecordMapper.insert(approvalRecord);

            //动态决定工单的实际审批者
            actualApproverFinalizer.decideActualApprover(workOrderVo, true);

            WorkOrder workOrder = new WorkOrder();

            //更新工单
            workOrder.setId(workOrderVo.getId());
            workOrder.setStatus(0);
            workOrder.setIsExamined(1);
            workOrder.setFlowNodeId(nextFlowNodeId);
            workOrder.setActualApproverId(workOrderVo.getActualApproverId());

            workOrderDao.updateById(workOrder);

            //如果流转后工单的审批者是逻辑而不是某个人，处理该逻辑
            WorkOrderVo workOrderVoAfterUpdate = workOrderDao.getOneWorkOrderById(workOrderVo.getId()).getResult();
            Long actualApproverId = workOrderVoAfterUpdate.getActualApproverId();
            if (approverIsLogic(actualApproverId)) {
                processLogicApprover(actualApproverId, workOrderVo.getId());
            }

        }

    }

    private boolean approverIsLogic( Long actualApproverId ) {
        return actualApproverId < 0;
    }
}
