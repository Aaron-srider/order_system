package cn.edu.bistu.admin.user.rest;

import cn.edu.bistu.admin.user.Service.UserService;
import cn.edu.bistu.common.rest.BaseController;
import cn.edu.bistu.common.utils.Pagination;
import cn.edu.bistu.model.common.result.Result;
import cn.edu.bistu.model.common.result.ServiceResult;
import cn.edu.bistu.model.common.validation.ConditionQuery;
import cn.edu.bistu.model.entity.auth.User;
import cn.edu.bistu.model.vo.PageVo;
import cn.edu.bistu.model.vo.UserVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 管理员管理用户信息接口
 */
@RestController
@Slf4j
@CrossOrigin
public class AdminUserController extends BaseController{

    @Autowired
    UserService userService;

    /**
     * 返回所有用户的信息
     * @param pageVo 分页数据
     * @param userVo 筛选信息，用户角色为必填信息（roleCategory）
     * @return 符合条件的所有角色
     */
    @GetMapping("/users")
    public Result getAllUsers(PageVo pageVo,
                              @Validated({ConditionQuery.class}) UserVo userVo) {
        pageVo = Pagination.setDefault(pageVo.getCurrent(), pageVo.getSize());
        Page<UserVo> page = new Page<>(pageVo.getCurrent(), pageVo.getSize());
        //获取结果
        ServiceResult<UserVo> serviceResult = userService.getAllUsers(page, userVo);
        return Result.ok(serviceResult.getServiceResult());
    }


    /**
     * 锁定/解锁用户，之后该用户无法登录
     * @param id 用户id
     * @param status 1表示锁定，0表示解锁
     */
    @PutMapping("/lock/{id}/{status}")
    public Result lock(
            @PathVariable("id") @NotNull Long id,
            @PathVariable("status") @Pattern(regexp = "[0|1]") Integer status
            ) {

        User user = new User();
        user.setIsLock(status);
        user.setId(id);
        userService.lock(user);

        return Result.ok();
    }

    /**
     * 更新用户信息，要求参数见文档
     * @param userVo 包含了更新用户所要求的的所有参数
     */
    @PutMapping("/users")
    public Result update(
           @RequestBody @Validated UserVo userVo) {
        ServiceResult<UserVo> serviceResult = userService.updateUser(userVo);
        return Result.ok(serviceResult.getServiceResult());
    }

    @PostMapping("/admin/user/promote/{userId}")
    public Result promote(
           @NotNull @PathVariable(name="userId") Long userId) {
        userService.promote(userId);
        return Result.ok();
    }

    /**
     * 将管理员用户降级为普通用户
     * @param userId 用户id
     */
    @DeleteMapping("/admin/user/demote/{userId}")
    public Result demote(
            @NotNull @PathVariable(name="userId") Long userId) {
        userService.demote(userId);
        return Result.ok();
    }


    /**
     * 根据用户工号查询用户信息
     * @param studentJobId 用户工号
     * @return 查询到的用户信息
     */
    @GetMapping("/user/{studentJobId}")
    public Result searchByStudentJobId(
            @NotNull @PathVariable(name="studentJobId") String studentJobId) {
        return Result.ok(userService.searchOneUserByStudentJobId(studentJobId).getServiceResult());
    }


}
