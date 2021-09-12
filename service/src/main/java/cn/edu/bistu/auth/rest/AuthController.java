package cn.edu.bistu.auth.rest;


import cn.edu.bistu.auth.service.AuthService;
import cn.edu.bistu.common.MapService;
import cn.edu.bistu.common.config.ParamIntegrityChecker;
import cn.edu.bistu.common.exception.FrontDataMissingException;
import cn.edu.bistu.common.exception.InterfaceAccessException;
import cn.edu.bistu.common.validation.UserRoleValue;
import cn.edu.bistu.constants.ResultCodeEnum;
import cn.edu.bistu.model.common.CheckUserRole;
import cn.edu.bistu.model.common.Result;
import cn.edu.bistu.model.common.ServiceResult;
import cn.edu.bistu.model.vo.UserVo;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@RestController
@Slf4j
@Validated
public class AuthController {

    @Autowired
    AuthService authService;

    @GetMapping("/auth/login")
    public Result login(@NotNull String code) {
        ServiceResult<JSONObject> result = authService.authentication(code);
        JSONObject serviceResult = result.getServiceResult();
        return Result.ok(serviceResult);
    }

    @PutMapping("/auth/userInfoCompletion/{roleCase}")
    public Result completeUserInfo(
            @PathVariable("roleCase")
            @UserRoleValue(roleCases={"student","teacher"}, message = "类型必须为 student|teacher") String roleCase,
            @RequestBody @Validated UserVo userVo) {
        checkUserRole(userVo.getRoleId().intValue(), roleCase);
        authService.userInfoCompletion(userVo);
        return Result.ok();
    }

    private void checkUserRole(Integer role_id, String roleString) {
        Long roleId = role_id.longValue();

        String roleCase = CheckUserRole.checkUserRole(roleId);

        if (!roleCase.equals(roleString)) {
            throw new InterfaceAccessException(null, ResultCodeEnum.INTERFACE_ACCESS_ERRORS);
        }
    }
}
