package cn.edu.bistu.utils;

import cn.edu.bistu.dept.service.DeptService;
import cn.edu.bistu.model.common.result.Result;
import cn.edu.bistu.model.common.result.ServiceResult;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class UtilsController {

    @Autowired
    DeptService deptService;

    @GetMapping("/utils/commonInfo")
    public Result commonInfo() {
        ServiceResult<JSONObject> commonInfo = deptService.getAllDeptCollegeMajor();
        JSONObject serviceResult = commonInfo.getServiceResult();
        return Result.ok(serviceResult);
    }

}
