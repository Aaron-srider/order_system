package cn.edu.bistu.dept.service;

import cn.edu.bistu.dept.mapper.DeptDao;
import cn.edu.bistu.model.common.result.ServiceResult;
import cn.edu.bistu.model.common.result.ServiceResultImpl;
import cn.edu.bistu.model.entity.Clazz;
import cn.edu.bistu.model.entity.College;
import cn.edu.bistu.model.entity.Major;
import cn.edu.bistu.model.entity.SecondaryDept;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeptServiceImpl implements DeptService {

    @Autowired
    DeptDao deptDao;

    @Override
    public ServiceResult<JSONObject> getAllDeptCollegeMajor() {
        List<Major> majorList = deptDao.getMajorMapper().selectList(null);
        List<SecondaryDept> secondaryDeptList = deptDao.getSecondaryDeptMapper().selectList(null);
        List<College> collegeList = deptDao.getCollegeMapper().selectList(null);
        List<Clazz> clazzList = deptDao.getClazzMapper().selectList(null);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("majorList", majorList);
        jsonObject.put("secondaryDeptList", secondaryDeptList);
        jsonObject.put("collegeList", collegeList);
        jsonObject.put("clazzList", clazzList);

        return new ServiceResultImpl<JSONObject>(jsonObject);
    }
}
