package cn.edu.bistu.auth.rest;


import cn.edu.bistu.auth.service.AuthService;
import cn.edu.bistu.common.MapService;
import cn.edu.bistu.common.config.ParamIntegrityChecker;
import cn.edu.bistu.common.exception.FrontDataMissingException;
import cn.edu.bistu.common.exception.InterfaceAccessException;
import cn.edu.bistu.constants.ResultCodeEnum;
import cn.edu.bistu.model.common.Result;
import cn.edu.bistu.model.common.ServiceResult;
import cn.edu.bistu.model.vo.UserVo;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@CrossOrigin
public class AuthController {

    @Autowired
    AuthService authService;

    @Autowired
    ParamIntegrityChecker paramIntegrityChecker;

    @GetMapping("/auth/login")
    public Result login(String code) {
        ServiceResult<JSONObject> result = authService.authentication(code);
        JSONObject serviceResult = result.getServiceResult();
        return Result.ok(serviceResult);
    }

    @PutMapping("/auth/userInfoCompletion/{roleCase}")
    public Result completeUserInfo(
            @PathVariable("roleCase") String roleCase,
            @RequestBody MapService mapService) {
        UserVo userVo = handleUserParams(mapService, roleCase);
        authService.userInfoCompletion(userVo);
        return Result.ok();
    }

    private UserVo handleUserParams(MapService mapService, String expectedRole) {
        //检查完善信息的用户的角色是否是教师
        Integer role_id = (Integer) mapService.get("roleId");
        String roleCase = checkUserRole(role_id, expectedRole);

        checkUserParamIntegrity(mapService, roleCase);

        UserVo userVo = assembleUser(mapService, roleCase);
        userVo.setRoleId(role_id.longValue());
        return userVo;
    }

    private void checkUserParamIntegrity(MapService mapService, String userRole) {
        String[] requiredParams = null;
        switch (userRole) {
            case "teacher":
                requiredParams = new String[]{"id", "roleId", "name", "collegeName", "secondaryDeptName", "jobId"};
                break;
            case "student":
                requiredParams = new String[]{"id", "roleId", "name", "collegeName", "majorName", "className", "grade", "studentId"};
                break;
            default:
                throw new InterfaceAccessException("unknown role:" + userRole, ResultCodeEnum.INTERFACE_ACCESS_ERRORS);
        }
        paramIntegrityChecker.setRequiredPropsName(requiredParams);
        paramIntegrityChecker.checkMapParamIntegrity(mapService);
    }

    private String checkUserRole(Integer role_id, String roleString) {

        if (role_id == null) {
            throw new FrontDataMissingException("param missing: roleId", ResultCodeEnum.FRONT_DATA_MISSING);
        }

        Long roleId = role_id.longValue();

        String roleCase;
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

        if (!roleCase.equals(roleString)) {
            throw new InterfaceAccessException(null, ResultCodeEnum.INTERFACE_ACCESS_ERRORS);
        }
        return roleCase;
    }

    private UserVo assembleUser(MapService mapService, String roleCase) {
        UserVo userVo = new UserVo();
        switch (roleCase) {
            case "student":
                userVo.setId((mapService.getVal("id", Integer.class)).longValue());
                userVo.setName(mapService.getVal("name", String.class));
                userVo.setCollegeName(mapService.getVal("collegeName", String.class));
                userVo.setMajorName(mapService.getVal("majorName", String.class));
                userVo.setClassName(mapService.getVal("className", String.class));
                userVo.setGrade(mapService.getVal("grade", Integer.class));
                userVo.setStudentId(mapService.getVal("studentId", String.class));
                break;
            case "teacher":
                userVo.setId((mapService.getVal("id", Integer.class)).longValue());
                userVo.setName(mapService.getVal("name", String.class));
                userVo.setCollegeName(mapService.getVal("collegeName", String.class));
                userVo.setSecondaryDeptName(mapService.getVal("secondaryDeptName", String.class));
                userVo.setJobId(mapService.getVal("jobId", String.class));
                break;
        }
        return userVo;
    }

}
