package cn.edu.bistu.auth.rest;


import cn.edu.bistu.auth.service.AuthService;
import cn.edu.bistu.auth.service.UserService;
import cn.edu.bistu.model.common.Result;
import cn.edu.bistu.model.vo.UserVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
@CrossOrigin
public class AuthController {

    @Autowired
    AuthService authService;

    @Autowired
    UserService userService;

    @PostMapping("/auth/login")
    public Result login(@RequestBody String code) {
        JSONObject parse = JSON.parseObject(code);
        code = (String) parse.get("code");

        Result result = authService.authentication(code);
        return result;
    }

    @PostMapping("/auth/userInfoCompletion")
    public Result completeUserInfo(@RequestBody UserVo userVo, HttpServletRequest req) {
        Result result = userService.userInfoCompletion(userVo);
        return result;
    }

}
