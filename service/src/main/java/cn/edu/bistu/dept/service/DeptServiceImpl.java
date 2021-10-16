package cn.edu.bistu.dept.service;

import cn.edu.bistu.dept.mapper.DeptDao;
import cn.edu.bistu.workOrder.mapper.WorkOrderDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeptServiceImpl implements DeptService {

    @Autowired
    DeptDao deptDao;

    @Autowired
    WorkOrderDaoImpl workOrderDaoImpl;

    //@Override
    //public ServiceResult<JSONObject> getAllDeptCollegeMajor() {
    //    List<Major> majorList = deptDao.getMajorMapper().selectList(null);
    //    List<SecondaryDept> secondaryDeptList = deptDao.getSecondaryDeptMapper().selectList(null);
    //    List<College> collegeList = deptDao.getCollegeMapper().selectList(null);
    //    List<WorkOrderStatus> workOrderStatusList = workOrderDao.getWorkOrderStatusMapper().selectList(null);
    //
    //    JSONObject jsonObject = new JSONObject();
    //    jsonObject.put("majorList", majorList);
    //    jsonObject.put("secondaryDeptList", secondaryDeptList);
    //    jsonObject.put("collegeList", collegeList);
    //    jsonObject.put("workOrderStatusList", collegeList);
    //
    //    return new ServiceResultImpl<JSONObject>(jsonObject);
    //}
}
