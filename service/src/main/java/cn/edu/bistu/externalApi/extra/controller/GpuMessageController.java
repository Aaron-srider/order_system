package cn.edu.bistu.externalApi.extra.controller;

import cn.edu.bistu.constants.ResultCodeEnum;
import cn.edu.bistu.externalApi.extra.exception.GpuSysErrorException;
import cn.edu.bistu.message.service.MessageService;
import cn.edu.bistu.model.common.result.Result;
import cn.edu.bistu.model.entity.Message;
import cn.edu.bistu.model.entity.auth.User;
import cn.edu.bistu.user.Service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GpuMessageController {

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @PostMapping("/gpu/api/message")
    public Result<Object> messageHandler(@Validated @RequestBody Message message) {
        message.setIsShowSender(1);
        message.setSender(-4L);
        long receiverId = message.getReceiver();

        // 两种查询方式，如果为负，则为学/工号查询 （由于工单系统原因，学/工号不一定唯一，故不推荐使用）
        if (message.getReceiver() < 0) {
            User u = userService
                    .getOne(new QueryWrapper<User>().eq("student_job_id", String.valueOf(-receiverId)));
            if (u == null)
                throw new GpuSysErrorException("用户工号" + -receiverId + " 不存在");
            message.setReceiver(u.getId());
        }

        messageService.sendMessage(message);
        return Result.build("", ResultCodeEnum.SUCCESS.getCode(), "Success");
    }
}
