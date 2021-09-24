package cn.edu.bistu.auth.rest;


import cn.edu.bistu.auth.service.AuthService;
import cn.edu.bistu.common.exception.InterfaceAccessException;
import cn.edu.bistu.common.validation.UserRoleValue;
import cn.edu.bistu.constants.ResultCodeEnum;
import cn.edu.bistu.model.common.CheckUserRole;
import cn.edu.bistu.model.common.result.Result;
import cn.edu.bistu.model.common.result.ServiceResult;
import cn.edu.bistu.model.vo.UserVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@RestController
@Slf4j
@Validated
@CrossOrigin
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping("/vue-admin-template/user/login")
    public Result vue_login(@RequestBody JSONObject jsonObject) {
        UserVo userVo = new UserVo();
        userVo.setToken("0293i89jg89yghe893hefuhap");
        return Result.ok(userVo);
    }
    @GetMapping("/vue-admin-template/user/info")
    public Result vue_info() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", "wc");
        jsonObject.put("avatar", "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fpic3.zhimg.com%2F50%2Fv2-5095f9e4ef2c7eea1b768b5647eebb42_hd.jpg&refer=http%3A%2F%2Fpic3.zhimg.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1634889232&t=948fe5d799523fc824fefa8d1f055893");
        return Result.ok(jsonObject);
    }

    @GetMapping("/auth/login")
    public Result login(@NotNull String code) {
        ServiceResult<JSONObject> result = authService.authentication(code);
        JSONObject serviceResult = result.getServiceResult();
        return Result.ok(serviceResult);
    }

    @PutMapping("/auth/userInfoCompletion")
    public Result completeUserInfo(
            @RequestBody @Validated UserVo userVo) {
        ServiceResult<JSONObject> serviceResult=authService.userInfoCompletion(userVo);
        return Result.ok(serviceResult.getServiceResult());
    }


    private void checkUserRole(Integer role_id, String roleString) {
        Long roleId = role_id.longValue();

        String roleCase = CheckUserRole.checkUserRole(roleId);

        if (!roleCase.equals(roleString)) {
            throw new InterfaceAccessException(null, ResultCodeEnum.INTERFACE_ACCESS_ERRORS);
        }
    }
}
