package cn.edu.bistu.message.rest;

import cn.edu.bistu.common.MapService;
import cn.edu.bistu.common.exception.ResultCodeException;
import cn.edu.bistu.common.utils.MimeTypeUtils;
import cn.edu.bistu.constants.ResultCodeEnum;
import cn.edu.bistu.message.exception.MessageException;
import cn.edu.bistu.message.service.MessageService;
import cn.edu.bistu.model.common.result.Result;
import cn.edu.bistu.model.common.result.ServiceResult;
import cn.edu.bistu.model.entity.Message;

import cn.edu.bistu.model.vo.MessageVo;
import cn.edu.bistu.model.vo.PageVo;
import cn.edu.bistu.workOrder.exception.AttachmentNotExistsException;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
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
@CrossOrigin
public class MessageController {

    @Autowired
    MessageService messageService;


    Logger logger =  LoggerFactory.getLogger(MessageController.class);

    //获取收件箱
    @GetMapping("/getReceiveMsg")
    public Result getReceiveMessage(PageVo pageVo,
                                    Message message,
                                    HttpServletRequest request){

        Page<MessageVo> page = getMsgCommon(pageVo, message, request);
        IPage<MessageVo> receiveMessages = messageService.getReceiveMessages(page, message);
        return Result.ok(receiveMessages);
    }

    //获取发件箱
    @GetMapping("/getSendMsg")
    public Result getSendMessage(PageVo pageVo, Message message,
                                                    HttpServletRequest request) {

        Page<MessageVo> page = getMsgCommon(pageVo, message, request);
        IPage<MessageVo> sendMessages = messageService.getSendMessages(page, message);
        return Result.ok(sendMessages);
    }

    public Page<MessageVo> getMsgCommon(PageVo pageVo, Message message, HttpServletRequest request) {
        if (pageVo.getSize() == 0) {
            pageVo.setSize(10);
        }

        if (pageVo.getCurrent() == 0) {
            pageVo.setCurrent(1);
        }

        MapService userInfo = (MapService) request.getAttribute("userInfo");
        Long visitorId = userInfo.getVal("id", Long.class);
        //将用户id设置到message中，xml中直接使用这个id作为sender或者是receiver进行where过滤
        message.setId(visitorId);
        Page<MessageVo> page = new Page<>(pageVo.getCurrent(), pageVo.getSize());
        return page;
    }

    @GetMapping("/messageDetail/{messageId}")
    public Result messageDetail(@PathVariable("messageId") Long messageId, HttpServletRequest request){

        Message message =  messageService.getMessageById(messageId);
        //判断获取的是发送消息详情  或者是收到的消息详情
        MapService userInfo = (MapService) request.getAttribute("userInfo");
        Long userId = userInfo.getVal("id", Long.class);
        boolean userIsSender;
        if (userId == message.getSender()) { //如果获取的是发送消息的详情，需要获取收件人信息
            userIsSender = true;
            userId = message.getReceiver();
        } else if (userId == message.getReceiver()) {//如果获取的是收到的消息的详情，需要获取发件人信息
            userIsSender = false;
            userId = message.getSender();
        } else {
            //后端逻辑错误，返回给用户的消息不属于用户
            return Result.build(null,ResultCodeEnum.BACKEND_ERROR);
        }
        MessageVo messageVo = messageService.messageDetail(messageId, userId);
        if (!userIsSender) { //如果获取的是收取消息的详情，那么设置消息为已读
            message.setStatus(1);
            messageService.updateMessage(message);
        }
        return Result.ok(messageVo);
    }


    @PostMapping("/sendMessage")
    public Result sendMessage(@Validated @RequestBody Message message, HttpServletRequest request) {

        MapService userInfo = (MapService) request.getAttribute("userInfo");

        Long sender = userInfo.getVal("id", Long.class);
        if (sender == message.getReceiver()) {
            return Result.build(null,ResultCodeEnum.SENDER_IS_RECEIVER);
        }
        message.setSender(sender);
        Long id = messageService.sendMessage(message);
        return Result.ok(id);
    }

    @PutMapping("/upLoadAttachment/{messageId}")
    public Result upLoadAttachment(@PathVariable("messageId") Long messageId,

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
            message.setAttachment(bytes);
            message.setAttachmentName(attachment.getOriginalFilename());
            messageService.updateMessage(message);
            return Result.ok();
        } else {
            return Result.build(null,ResultCodeEnum.FRONT_DATA_MISSING);
        }
    }

    @GetMapping("/downLoadAttachment/{messageId}/{attachmentDownloadId}")
    public void downLoadAttachment(@PathVariable("messageId") Long messageId,
                                   @PathVariable("attachmentDownloadId") String attachmentDownloadId,
                                  HttpServletResponse resp) throws ResultCodeException, IOException {

        //查询附件
        Message message = messageService.getAttachment(messageId);
        if (message == null) {
            throw new MessageException(null,ResultCodeEnum.MESSAGE_NOT_EXIST);
        }
        byte[] attachmentBytes = message.getAttachment();

        //log.debug("" + attachmentBytes.length);

        if (attachmentBytes == null) {
            throw new AttachmentNotExistsException(null, ResultCodeEnum.ATTACHMENT_NOT_EXISTS);
        }

        if (message.getAttachmentDownloadId() != null &&
                message.getAttachmentDownloadId().equals(attachmentDownloadId)) {
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
