package cn.edu.bistu.approval;

import cn.edu.bistu.approval.mapper.ApprovalRecordMapper;
import cn.edu.bistu.constants.ApprovalOperationEnum;
import cn.edu.bistu.constants.WorkOrderStatus;
import cn.edu.bistu.model.entity.ApprovalRecord;
import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.model.entity.WorkOrderHistory;
import cn.edu.bistu.workOrder.dao.WorkOrderDao;
import cn.edu.bistu.workOrder.dao.WorkOrderHistoryDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@Component
public class BaseWorkOrderFinisher {

    @Autowired
    ApprovalRecordMapper approvalRecordMapper;

    @Qualifier("workOrderDaoImpl")
    @Autowired
    WorkOrderDao workOrderDao;

    @Autowired
    WorkOrderHistoryDao workOrderHistoryDao;

    protected void finishWorkOrder0(WorkOrderFinishContext workOrderFinishWrap) {
        WorkOrder fullPreparedWorkOrderToBeFinished = workOrderFinishWrap.fullPreparedWorkOrderToBeFinished;
        WorkOrderStatus finishStatusConstant = workOrderFinishWrap.finishStatusConstant;
        Long workOrderId = fullPreparedWorkOrderToBeFinished.getId();

        //设置结束工单状态
        WorkOrder workOrder = populateWorkOrder2BeFinished(workOrderId, finishStatusConstant);
        workOrderDao.updateById(workOrder);

        //插入工单历史
        WorkOrderHistory workOrderHistory =
                generateWorkOrderHistory(
                        workOrderId,
                        fullPreparedWorkOrderToBeFinished.getStatus());
        workOrderHistoryDao.insertOne(workOrderHistory);
    }

    protected WorkOrder populateWorkOrder2BeFinished(Long workOrderId, WorkOrderStatus workOrderStatus) {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setId(workOrderId);
        workOrder.setIsFinished(1);
        workOrder.setIsExamined(1);
        workOrder.setStatus(workOrderDao.constantToEntity(workOrderStatus).getValue());
        return workOrder;
    }

    protected WorkOrderHistory generateWorkOrderHistory(Long workOrderId, Integer WorkOrderStatus) {
        WorkOrderHistory workOrderHistory = new WorkOrderHistory();
        workOrderHistory.setWorkOrderId(workOrderId);
        workOrderHistory.setBeforeFinishedStatus(WorkOrderStatus);
        return workOrderHistory;
    }

    public void prepareApprovalRecord(ApprovalRecord approvalRecord, Long flowNodeId, ApprovalOperationEnum approvalOperation) {
        approvalRecord.setFlowNodeId(flowNodeId);
        approvalRecord.setOperation(approvalOperation.getCode());
    }

    /**
     * 生成对应工单的历史记录
     * @param workOrder 工单
     */
    public void generateWorkOrderHistory(cn.edu.bistu.model.entity.WorkOrderStatus beforeStatus, WorkOrder workOrder) {
        //生成历史工单
        WorkOrderHistory workOrderHistory = new WorkOrderHistory();
        workOrderHistory.setWorkOrderId(workOrder.getId());
        workOrderHistory.setBeforeFinishedStatus(beforeStatus.getValue());
        workOrderHistory.setBeforeFinishedFlowNodeId(workOrder.getFlowNodeId());
        workOrderHistoryDao.insertOne(workOrderHistory);
    }

    public void commonRoutine(WorkOrder workOrder, WorkOrderStatus finishStatus) {
        //记录工单结束前的状态
        cn.edu.bistu.model.entity.WorkOrderStatus beforeFinished = new cn.edu.bistu.model.entity.WorkOrderStatus();
        beforeFinished.setValue(workOrder.getStatus());

        //更新工单状态
        cn.edu.bistu.model.entity.WorkOrderStatus workOrderStatus = workOrderDao.constantToEntity(finishStatus);
        workOrder.setStatus(workOrderStatus.getValue());

        //工单结束
        workOrder.setIsFinished(1);
        workOrderDao.updateById(workOrder);

        //生成历史工单
        generateWorkOrderHistory(beforeFinished, workOrder);

        //发送微信通知
        //Long initiatorId = workOrder.getInitiatorId();
        //UserVo userVo = userMapper.getOneById(initiatorId);
        //String openId = userVo.getOpenId();
        ////模板还没选好，此步跳过
        //wxMiniApi.sendSubscribeMsg(openId);
    }
}
