package cn.edu.bistu.dept.mapper;

import cn.edu.bistu.model.common.result.DaoResult;
import cn.edu.bistu.model.common.result.DaoResultImpl;
import cn.edu.bistu.model.entity.College;
import cn.edu.bistu.model.entity.Major;
import cn.edu.bistu.model.entity.SecondaryDept;
import cn.edu.bistu.model.entity.WorkOrderStatus;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Getter
public class DeptDao {

    @Autowired
    CollegeMapper collegeMapper;

    @Autowired
    MajorMapper majorMapper;

    @Autowired
    SecondaryDeptMapper secondaryDeptMapper;

    public DaoResult<JSONObject> getAllCollegeMajorDept() {

        List<Major> majorList = getMajorMapper().selectList(null);
        List<SecondaryDept> secondaryDeptList = getSecondaryDeptMapper().selectList(null);
        List<College> collegeList = getCollegeMapper().selectList(null);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("majorList", majorList);
        jsonObject.put("secondaryDeptList", secondaryDeptList);
        jsonObject.put("collegeList", collegeList);

        DaoResult<JSONObject> objectDaoResult = new DaoResultImpl<>();
        objectDaoResult.setResult(jsonObject);

        return objectDaoResult;
    }
}
