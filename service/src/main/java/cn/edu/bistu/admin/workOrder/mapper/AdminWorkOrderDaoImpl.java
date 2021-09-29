package cn.edu.bistu.admin.workOrder.mapper;

import cn.edu.bistu.approval.mapper.ApprovalRecordMapper;
import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.workOrder.mapper.WorkOrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AdminWorkOrderDaoImpl extends WorkOrderDao implements AdminWorkOrderDao{

    @Autowired
    ApprovalRecordMapper approvalRecordMapper;

    @Override
    public void deleteWorkOrderByWorkOrderId(Long workOrderId) {
        getWorkOrderMapper().deleteWorkOrderByWorkOrderId(workOrderId);
    }

    @Override
    public void deleteWorkOrderHistoryByWorkOrderId(Long workOrderId) {
        getWorkOrderHistoryMapper().deleteWorkOrderHistoryByWorkOrderId(workOrderId);
    }

    @Override
    public void deleteWorkOrderApprovalRecordsByWorkOrderId(Long workOrderId) {
        approvalRecordMapper.deleteWorkOrderApprovalRecordsByWorkOrderId(workOrderId);
    }

    @Override
    public void updateWorkOrderById(WorkOrder workOrder) {
        getWorkOrderMapper().updateById(workOrder);
    }

    @Override
    public void changeWorkOrderStatusToInvalidation(Long id) {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setId(id);
        workOrder.setStatus(4);
        getWorkOrderMapper().updateById(workOrder);
    }
}
