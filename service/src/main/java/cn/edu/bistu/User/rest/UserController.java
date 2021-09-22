package cn.edu.bistu.User.rest;

import cn.edu.bistu.User.Service.UserService;
import cn.edu.bistu.common.rest.BaseController;
import cn.edu.bistu.model.common.result.Result;
import cn.edu.bistu.model.common.result.ServiceResult;
import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.model.entity.auth.User;
import cn.edu.bistu.model.vo.PageVo;
import cn.edu.bistu.model.vo.UserVo;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.IOException;

@RestController
@Slf4j
public class UserController extends BaseController{

    @Autowired
    UserService userService;

    @GetMapping("/users")
    public Result getAllUsers(PageVo pageVo,
                              HttpServletResponse resp) {

        if (pageVo.getSize() == null) {
            pageVo.setSize(10);
        }
        if (pageVo.getCurrent() == null) {
            pageVo.setCurrent(1);
        }
        Page<UserVo> page = new Page<>(pageVo.getCurrent(), pageVo.getSize());
        //获取结果
        ServiceResult<JSONObject> serviceResult = userService.getAllUsers(page);
        JSONObject result = serviceResult.getServiceResult();
        log.debug(result + "");
        cors(resp);
        return Result.ok(result);
    }


    @PutMapping("/lock/{id}/{status}")
    public Result lock(
            @PathVariable("id") @NotNull Long id,
            @PathVariable("status") @Pattern(regexp = "[0|1]") Integer status,
            HttpServletResponse resp) {

        User user = new User();
        user.setIsLock(status);
        user.setId(id);
        userService.lock(user);

        cors(resp);
        return Result.ok();
    }



}
