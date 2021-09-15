package cn.edu.bistu.message.rest;

import cn.edu.bistu.common.MapService;
import cn.edu.bistu.common.exception.ResultCodeException;
import cn.edu.bistu.common.exception.WorkOrderNotExistsException;
import cn.edu.bistu.common.utils.MimeTypeUtils;
import cn.edu.bistu.constants.ResultCodeEnum;
import cn.edu.bistu.message.exception.MessageException;
import cn.edu.bistu.message.service.MessageService;
import cn.edu.bistu.model.common.result.Result;
import cn.edu.bistu.model.common.result.ServiceResult;
import cn.edu.bistu.model.entity.Message;

import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.model.vo.MessageVo;
import cn.edu.bistu.model.vo.PageVo;
import cn.edu.bistu.workOrder.exception.AttachmentNotExistsException;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sun.deploy.net.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * @author: Mr.Yu
 * @create: 2021-09-08 23:07
 **/
@RequestMapping("/message")
@RestController
public class MessageController {

    @Autowired
    MessageService messageService;

    Logger logger =  LoggerFactory.getLogger(MessageController.class);

    //获取收件箱
    @PostMapping("/getReceiveMsg")
    public ServiceResult<JSONObject> getReceiveMessage(@RequestBody PageVo pageVo,
                                    HttpServletRequest request){

        if (pageVo.getSize() == 0) {
            pageVo.setSize(10);
        }

        if (pageVo.getCurrent() == 0) {
            pageVo.setCurrent(1);
        }

        MapService userInfo = (MapService) request.getAttribute("userInfo");
        Long visitorId = userInfo.getVal("id", Long.class);
        Page<MessageVo> page = new Page<>(pageVo.getCurrent(), pageVo.getSize());
        ServiceResult<JSONObject> result = messageService.getReceiveMessageById(page,visitorId);
        return result;
    }

    //获取发件箱
    @PostMapping("/getSendMsg")
    public ServiceResult<JSONObject> getSendMessage(@RequestBody PageVo pageVo,
                                 HttpServletRequest request) {

        if (pageVo.getSize() == 0) {
            pageVo.setSize(10);
        }

        if (pageVo.getCurrent() == 0) {
            pageVo.setCurrent(1);
        }

        MapService userInfo = (MapService) request.getAttribute("userInfo");
        Long visitorId = userInfo.getVal("id", Long.class);
        Page<MessageVo> page = new Page<>(pageVo.getCurrent(), pageVo.getSize());
        ServiceResult<JSONObject> result = messageService.getSendMessageById(page,visitorId);
        return result;
    }

    @GetMapping("/messageDetail/{messageId}")
    public Result MessageDetail(@PathVariable("messageId") Long messageId){


        Message message = messageService.getMessageById(messageId);
        if (message == null) {
            logger.error("消息不存在");
            return Result.build(null,ResultCodeEnum.MESSAGE_NOT_EXIST);
        }
        //先不返回附件
        message.setContent(null);

        //设置消息为已读
        message.setStatus(1);
        messageService.updateMessage(message);

        return Result.ok(message);
    }

    @PostMapping("/sendMessage")
    public Result sendMessage(@Validated @RequestBody Message message, HttpServletRequest request) {


        MapService userInfo = (MapService) request.getAttribute("userInfo");
        Long sender = userInfo.getVal("id", Long.class);
        message.setSender(sender);
        Long id = messageService.sendMessageById(message);
        return Result.ok(id);
    }

    @PutMapping("/upLoadAttachment/{messageId}")
    public Result upContent(@PathVariable("messageId") Long messageId,
                            HttpServletRequest request,
                            @RequestPart("attachment") MultipartFile attachment) throws IOException {

        MapService userInfo = (MapService) request.getAttribute("userInfo");
        Long userId = (Long) userInfo.get("id");

        Message message = messageService.getMessageById(messageId);
        if (message == null) {
            return Result.build(null,ResultCodeEnum.MESSAGE_NOT_EXIST);
        }

        if (!userId.equals(message.getSender())){
            return Result.build(null,ResultCodeEnum.USER_SENDMSG_NOT_CONSISTENT);
        }

        if (attachment.getSize() != 0 && !attachment.getOriginalFilename().equals("")) {
            byte[] bytes = attachment.getBytes();
            message.setContent(bytes);
            message.setAttachmentName(attachment.getOriginalFilename());
            messageService.updateMessage(message);
            return Result.ok();
        } else {
            return Result.build(null,ResultCodeEnum.FRONT_DATA_MISSING);
        }
    }

    @GetMapping("/downLoadAttachment/{messageId}")
    public void downLoadContent(@PathVariable("messageId") Long messageId,
                                  HttpServletResponse resp) throws ResultCodeException, IOException {

        //查询附件
        Message message = messageService.getMessageById(messageId);
        if (message == null) {
            throw new MessageException(null,ResultCodeEnum.MESSAGE_NOT_EXIST);
        }
        byte[] attachmentBytes = message.getContent();

        //log.debug("" + attachmentBytes.length);

        if (attachmentBytes == null) {
            throw new AttachmentNotExistsException(null, ResultCodeEnum.ATTACHMENT_NOT_EXISTS);
        }

        //获取附件的MIME类型
        String mimeType = MimeTypeUtils.getType(message.getAttachmentName());
        //设置响应的MIME类型
        resp.setContentType(mimeType);

        logger.debug("mimeType:" + mimeType);

        //让浏览器以附件形式处理响应数据
        resp.setHeader("Content-Disposition", "downloadAttachment; fileName=" + URLEncoder.encode(message.getAttachmentName(), "UTF-8"));

        logger.debug("attachmentName:" + message.getAttachmentName());

        //将二进制附件写入到http响应体中
        ServletOutputStream out = resp.getOutputStream();
        out.write(attachmentBytes, 0, attachmentBytes.length);
    }

    @PutMapping("/deleteMsg/{messageId}")
    public Result deleteMessage(@PathVariable("messageId") Long messageId,
                                HttpServletRequest request) throws Exception {

        Message message = messageService.getMessageById(messageId);
        if (message == null) {
            return Result.build(null,ResultCodeEnum.MESSAGE_NOT_EXIST);
        }

        MapService userInfo = (MapService) request.getAttribute("userInfo");
        Long userId = (Long) userInfo.get("id");

        boolean isSender;
        //判断当前的用户删除的消息接收的还是发送的
        if (message.getSender().equals(userId)) {
            isSender = true;
        } else if (message.getReceiver().equals(userId)) {
            isSender = false;
        } else {
            return Result.build(null,ResultCodeEnum.USER_MESSAGE_NOT_CONSISTENT);
        }

        messageService.deleteMessage(message,isSender);
        return Result.ok();
    }

}
