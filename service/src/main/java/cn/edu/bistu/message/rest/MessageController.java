package cn.edu.bistu.message.rest;

import cn.edu.bistu.common.MapService;
import cn.edu.bistu.message.service.MessageService;
import cn.edu.bistu.model.common.Result;
import cn.edu.bistu.model.common.ServiceResult;
import cn.edu.bistu.model.entity.Message;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author: Mr.Yu
 * @create: 2021-09-08 23:07
 **/
@RequestMapping("/message")
@RestController
public class MessageController {

    @Autowired
    MessageService messageService;

    //获取收件箱
    @GetMapping("/getReceiveMsg")
    public Result getReceiveMessage(HttpServletRequest request){

        MapService userInfo = (MapService) request.getAttribute("userInfo");
        Long visitorId = userInfo.getVal("id", Long.class);
        ServiceResult<JSONObject> result = messageService.getReceiveMessageById(visitorId);
        return Result.ok(result.getServiceResult());
    }

    //获取发件箱
    @GetMapping("/getSendMsg")
    public Result getSendMessage(HttpServletRequest request) {

        MapService userInfo = (MapService) request.getAttribute("userInfo");
        Long visitorId = userInfo.getVal("id", Long.class);
        ServiceResult<JSONObject> result = messageService.getSendMessageById(visitorId);
        return Result.ok(result.getServiceResult());
    }

    @PostMapping("/sendMessage")
    public Result sendMessage(HttpServletRequest request) {

        return null;
    }

}
