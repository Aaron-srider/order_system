package cn.edu.bistu.approval;

import cn.edu.bistu.approval.mapper.ApprovalRecordMapper;
import cn.edu.bistu.common.BeanUtils;
import cn.edu.bistu.constants.ApprovalOperation;
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

    public void prepareApprovalRecord(ApprovalRecord approvalRecord, Long flowNodeId, ApprovalOperation approvalOperation) {
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
