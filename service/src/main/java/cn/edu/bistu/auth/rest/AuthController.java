package cn.edu.bistu.auth.rest;


import cn.edu.bistu.auth.service.AuthService;
import cn.edu.bistu.auth.service.UserService;
import cn.edu.bistu.common.config.ParamIntegrityChecker;
import cn.edu.bistu.common.exception.FrontDataMissingException;
import cn.edu.bistu.constants.ResultCodeEnum;
import cn.edu.bistu.model.common.Result;
import cn.edu.bistu.model.vo.UserVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sun.tools.corba.se.idl.IncludeGen;
import com.sun.xml.internal.xsom.impl.scd.Iterators;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@Slf4j
@CrossOrigin
public class AuthController {

    @Autowired
    AuthService authService;

    @Autowired
    UserService userService;

    @Autowired
    ParamIntegrityChecker paramIntegrityChecker;

    @PostMapping("/auth/login")
    public Result login(@RequestBody String code) {
        JSONObject parse = JSON.parseObject(code);
        code = (String) parse.get("code");

        Result result = authService.authentication(code);
        return result;
    }

    @PostMapping("/auth/userInfoCompletion")
    public Result completeUserInfo(@RequestBody Map<String, Object> map) {

        Long roleId = ((Integer) map.get("roleId")).longValue();
        if (roleId == null) {
            throw new FrontDataMissingException("param missing: roleId", ResultCodeEnum.FRONT_DATA_MISSING);
        }

        String roleCase = null;

        switch (roleId.intValue()) {
            case 3:
            case 4:
            case 5:
                roleCase = "teacher";
                break;
            case 6:
            case 7:
                roleCase = "student";
                break;
            default:
                roleCase = "admin";
        }

        UserVo userVo = new UserVo();
        //老师
        if (roleCase.equals("teacher")) {
            paramIntegrityChecker.setRequiredPropsName(new String[]{
                    "id", "roleId", "name", "collegeName", "secondaryDeptName", "jobId"
            });
            paramIntegrityChecker.checkMapParamIntegrity(map);

            userVo.setId(((Integer)map.get("id")).longValue());
            userVo.setName((String) map.get("name"));
            userVo.setCollegeName((String)map.get("collegeName"));
            userVo.setSecondaryDeptName((String)map.get("secondaryDeptName"));
            userVo.setJobId((String)map.get("jobId"));
        }
        //学生
        else if(roleCase.equals("student")) {
            paramIntegrityChecker.setRequiredPropsName(new String[]{
                    "id", "roleId", "name", "collegeName", "majorName", "className", "grade", "studentId"
            });
            paramIntegrityChecker.checkMapParamIntegrity(map);

            userVo.setId(((Integer)map.get("id")).longValue());
            userVo.setName((String) map.get("name"));
            userVo.setCollegeName((String)map.get("collegeName"));
            userVo.setMajorName((String)map.get("majorName"));
            userVo.setClassName((String)map.get("className"));
            userVo.setGrade((Integer)map.get("grade"));
            userVo.setStudentId((String)map.get("studentId"));
        }

        userService.userInfoCompletion(userVo, roleId);
        return Result.ok();
    }

}
