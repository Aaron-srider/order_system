package cn.edu.bistu.message.service;

import cn.edu.bistu.model.common.ServiceResult;
import cn.edu.bistu.model.entity.Message;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * @author: Mr.Yu
 * @create: 2021-09-11 19:48
 **/
public interface MessageService {

    /**
     * 根据用户id获取收件箱
     * @param visitorId
     * @return java.util.List<cn.edu.bistu.model.entity.Message>
     * */
    ServiceResult<JSONObject> getReceiveMessageById(Long visitorId);

    /**
     * 根据用户id获取发件箱
     * @param visitorId
     * @return java.util.List<cn.edu.bistu.model.entity.Message>
     * */
    ServiceResult<JSONObject> getSendMessageById(Long visitorId);

    /**
     * 发送工单请求，返回插入的工单id方便上传附件
     * @param message
     * @return java.lang.Integer
     * */
    Long sendMessageById(Message message);

    /**
     * 根据消息id获取消息
     * @param messageId
     * @return cn.edu.bistu.model.entity.Message
     * */
    Message getMessageById(Long messageId);

    void updateMessage(Message message);
}
