package cn.edu.bistu.admin.workOrder.service;

import cn.edu.bistu.admin.workOrder.mapper.AdminWorkOrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminWorkOrderServiceImpl implements AdminWorkOrderService {

    @Autowired
    AdminWorkOrderDao adminWorkOrderDao;

    @Transactional
    @Override
    public void deleteWorkOrdersByWorkOrderIdList(List<Long> workOrderIdList) {

        for (Long workOrderId : workOrderIdList) {
            adminWorkOrderDao.deleteWorkOrderByWorkOrderId(workOrderId);
            adminWorkOrderDao.deleteWorkOrderHistoryByWorkOrderId(workOrderId);
            adminWorkOrderDao.deleteWorkOrderApprovalRecordsByWorkOrderId(workOrderId);
        }

    }
}
