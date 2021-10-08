package cn.edu.bistu.admin.workOrder.service;

import cn.edu.bistu.admin.workOrder.mapper.AdminWorkOrderDao;
import cn.edu.bistu.common.exception.ResultCodeException;
import cn.edu.bistu.common.utils.WorkOrderUtils;
import cn.edu.bistu.constants.ResultCodeEnum;
import cn.edu.bistu.model.common.result.DaoResult;
import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.model.entity.WorkOrderStatus;
import cn.edu.bistu.workOrder.mapper.WorkOrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminWorkOrderServiceImpl implements AdminWorkOrderService {

    @Autowired
    AdminWorkOrderDao adminWorkOrderDao;

    @Autowired
    WorkOrderUtils workOrderUtils;

    @Transactional
    @Override
    public void deleteWorkOrdersByWorkOrderIdList(List<Long> workOrderIdList) {

        for (Long workOrderId : workOrderIdList) {
            adminWorkOrderDao.deleteWorkOrderByWorkOrderId(workOrderId);
            adminWorkOrderDao.deleteWorkOrderHistoryByWorkOrderId(workOrderId);
            adminWorkOrderDao.deleteWorkOrderApprovalRecordsByWorkOrderId(workOrderId);
        }

    }

    @Override
    public void updateWorkOrderByWorkOrderId(WorkOrder workOrder) {
        adminWorkOrderDao.updateWorkOrderById(workOrder);
    }

    @Override
    public void invalidationWorkOrder(Long id) throws NoSuchFieldException, IllegalAccessException {

        DaoResult<WorkOrder> oneWorkOrderById = ((WorkOrderDao) adminWorkOrderDao).getOneWorkOrderById(id);
        WorkOrder workOrder = oneWorkOrderById.getResult();

        //如果工单已经结束，不予作废
        if(workOrder.getIsFinished().equals(1)) {
            throw new ResultCodeException("workOrder id:" + workOrder.getId(), ResultCodeEnum.WORKORDER_BEEN_FINISHED);
        }

        workOrderUtils.workOrderFinish(workOrder, null, cn.edu.bistu.constants.WorkOrderStatus.INVALIDATION);
    }


}
