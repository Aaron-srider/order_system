package cn.edu.bistu.admin.workOrder.service;

import cn.edu.bistu.admin.workOrder.mapper.AdminWorkOrderDao;
import cn.edu.bistu.approval.WorkOrderFinisherFactory;
import cn.edu.bistu.approval.service.ApprovalService;
import cn.edu.bistu.common.exception.ResultCodeException;
import cn.edu.bistu.constants.ResultCodeEnum;
import cn.edu.bistu.model.common.result.DaoResult;
import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.model.entity.WorkOrderHistory;
import cn.edu.bistu.model.entity.WorkOrderStatus;
import cn.edu.bistu.model.vo.WorkOrderVo;
import cn.edu.bistu.workOrder.dao.WorkOrderDaoImpl;
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
    ApprovalService approvalService;

    @Autowired
    WorkOrderFinisherFactory workOrderFinisherFactory;

    @Override
    @Transactional
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
    public void invalidationWorkOrder(List<Long> idList){

        for (Long id : idList) {
            WorkOrderVo workOrderVo = adminWorkOrderDao.getOneWorkOrderById(id).getResult();

            //如果工单已经结束，不予作废
            if(workOrderVo.getIsFinished().equals(1)) {
                throw new ResultCodeException("workOrder id:" + workOrderVo.getId(), ResultCodeEnum.WORKORDER_BEEN_FINISHED);
            }

            approvalService.workOrderFinish(
                    workOrderFinisherFactory.getFinisher("notApprovalTypeV2"),
                    workOrderVo,
                    null,
                    cn.edu.bistu.constants.WorkOrderStatus.INVALIDATION,
                    null);
        }

    }

    @Transactional
    @Override
    public void enableWorkOrder(List<Long> idList) {

        for (Long id : idList) {
            DaoResult<WorkOrderVo> oneWorkOrderById = ((WorkOrderDaoImpl) adminWorkOrderDao).getOneWorkOrderById(id);
            WorkOrderVo workOrderVo = oneWorkOrderById.getResult();

            //如果工单不是作废状态，不予开启
            WorkOrderStatus invalidationStatus = ((WorkOrderDaoImpl) adminWorkOrderDao).constantToEntity(cn.edu.bistu.constants.WorkOrderStatus.INVALIDATION);
            if(!workOrderVo.getStatus().equals(invalidationStatus.getValue())) {
                throw new ResultCodeException("workOrder id:" + workOrderVo.getId(), ResultCodeEnum.WORKORDER_NOT_INVALIDATED);
            }

            //恢复工单状态
            WorkOrderHistory workOrderHistory = ((WorkOrderDaoImpl) adminWorkOrderDao).getWorkOrderHistoryMapper().selectOne(new QueryWrapper<WorkOrderHistory>().eq("work_order_id", workOrderVo.getId()));
            workOrderVo.setStatus(workOrderHistory.getBeforeFinishedStatus());
            workOrderVo.setIsFinished(0);
            ((WorkOrderDaoImpl) adminWorkOrderDao).getWorkOrderMapper().updateById(workOrderVo);

            //删除工单历史
            ((WorkOrderDaoImpl) adminWorkOrderDao).getWorkOrderHistoryMapper().deleteWorkOrderHistoryByWorkOrderId(workOrderVo.getId());
        }
    }


}
