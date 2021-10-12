package cn.edu.bistu.utils.service;

import cn.edu.bistu.admin.User.mapper.UserDao;
import cn.edu.bistu.dept.mapper.DeptDao;
import cn.edu.bistu.model.common.result.DaoResult;
import cn.edu.bistu.model.common.result.ServiceResult;
import cn.edu.bistu.model.common.result.ServiceResultImpl;
import cn.edu.bistu.workOrder.mapper.WorkOrderDao;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CommonInfoServiceImpl implements CommonInfoService {

    @Autowired
    WorkOrderDao workOrderDao;

    @Autowired
    UserDao userDao;

    @Autowired
    DeptDao deptDao;

    @Override
    public ServiceResult getCommonInfo() {
        DaoResult<JSONObject> allWorkOrderStatus = workOrderDao.getAllWorkOrderStatus();
        DaoResult<JSONObject> allRoles = userDao.getAllRoles();
        DaoResult<JSONObject> allCollegeMajorDept = deptDao.getAllCollegeMajorDept();

        JSONObject collegeMajorDeptInfo = allCollegeMajorDept.getResult();
        JSONObject roleInfo = allRoles.getResult();
        JSONObject workOrderStatusInfo = allWorkOrderStatus.getResult();
        JSONObject result = cn.edu.bistu.common.JsonUtils.mergeJSONObject(workOrderStatusInfo, collegeMajorDeptInfo, roleInfo);
        return new ServiceResultImpl<>(result);
    }
}
