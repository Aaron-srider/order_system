package cn.edu.bistu.message.rest;

import cn.edu.bistu.common.MapService;
import cn.edu.bistu.message.service.MessageService;
import cn.edu.bistu.model.common.Result;
import cn.edu.bistu.model.common.ServiceResult;
import cn.edu.bistu.model.entity.Message;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
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
    public Result sendMessage(@RequestBody Message message, HttpServletRequest request) {
        MapService userInfo = (MapService) request.getAttribute("userInfo");
        Long sender = userInfo.getVal("id", Long.class);
        message.setSender(sender);
        Long id = messageService.sendMessageById(message);
        return Result.ok(id);
    }

    @PutMapping("/upAttachment/{messageId}")
    public Result upContent(@PathVariable("messageId") Long messageId,
                            HttpServletRequest request,
                            @RequestPart("attachment") MultipartFile attachment) throws IOException {

        MapService userInfo = (MapService) request.getAttribute("userInfo");
        Long userId = (Long) userInfo.get("id");

        Message message = messageService.getMessageById(messageId);
        if (message == null) {
            //暂时
            return Result.fail("消息不存在");
        }

        if (userId != message.getSender()){
            //暂时
            return Result.fail("发送消息用户不一致");
        }

        if (attachment.getSize() != 0 && !attachment.getOriginalFilename().equals("")) {
            byte[] bytes = attachment.getBytes();
            message.setContent(bytes);
            message.setAttachmentName(attachment.getOriginalFilename());
            messageService.updateMessage(message);
            return Result.ok();
        } else {
            return Result.fail("附件不存在");
        }
    }


}
