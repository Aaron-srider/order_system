package cn.edu.bistu.admin.workOrder.service;

import cn.edu.bistu.admin.workOrder.mapper.AdminWorkOrderDao;
import cn.edu.bistu.common.exception.ResultCodeException;
import cn.edu.bistu.common.utils.WorkOrderUtils;
import cn.edu.bistu.constants.ResultCodeEnum;
import cn.edu.bistu.model.common.result.DaoResult;
import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.model.entity.WorkOrderHistory;
import cn.edu.bistu.model.entity.WorkOrderStatus;
import cn.edu.bistu.workOrder.mapper.WorkOrderDao;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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

    @Transactional
    @Override
    public void invalidationWorkOrder(List<Long> idList) throws NoSuchFieldException, IllegalAccessException {

        for (Long id : idList) {
            DaoResult<WorkOrder> oneWorkOrderById = ((WorkOrderDao) adminWorkOrderDao).getOneWorkOrderById(id);
            WorkOrder workOrder = oneWorkOrderById.getResult();

            //如果工单已经结束，不予作废
            if(workOrder.getIsFinished().equals(1)) {
                throw new ResultCodeException("workOrder id:" + workOrder.getId(), ResultCodeEnum.WORKORDER_BEEN_FINISHED);
            }

            workOrderUtils.workOrderFinish(workOrder, null, cn.edu.bistu.constants.WorkOrderStatus.INVALIDATION);
        }

    }

    @Transactional
    @Override
    public void enableWorkOrder(List<Long> idList) throws NoSuchFieldException, IllegalAccessException {

        for (Long id : idList) {
            DaoResult<WorkOrder> oneWorkOrderById = ((WorkOrderDao) adminWorkOrderDao).getOneWorkOrderById(id);
            WorkOrder workOrder = oneWorkOrderById.getResult();

            //如果工单不是作废状态，不予开启
            WorkOrderStatus invalidationStatus = ((WorkOrderDao) adminWorkOrderDao).constantToEntity(cn.edu.bistu.constants.WorkOrderStatus.INVALIDATION);
            if(!workOrder.getStatus().equals(invalidationStatus.getValue())) {
                throw new ResultCodeException("workOrder id:" + workOrder.getId(), ResultCodeEnum.WORKORDER_NOT_INVALIDATED);
            }

            //恢复工单状态
            WorkOrderHistory workOrderHistory = ((WorkOrderDao) adminWorkOrderDao).getWorkOrderHistoryMapper().selectOne(new QueryWrapper<WorkOrderHistory>().eq("work_order_id", workOrder.getId()));
            workOrder.setStatus(workOrderHistory.getBeforeFinishedStatus());
            workOrder.setIsFinished(0);
            ((WorkOrderDao) adminWorkOrderDao).getWorkOrderMapper().updateById(workOrder);

            //删除工单历史
            ((WorkOrderDao) adminWorkOrderDao).getWorkOrderHistoryMapper().deleteWorkOrderHistoryByWorkOrderId(workOrder.getId());
        }
    }


}
