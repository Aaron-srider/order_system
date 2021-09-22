package cn.edu.bistu.message.service;

import cn.edu.bistu.model.common.result.ServiceResult;
import cn.edu.bistu.model.entity.Message;
import cn.edu.bistu.model.vo.MessageVo;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

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
    ServiceResult<JSONObject> getReceiveMessageById(Page<MessageVo> page, Long visitorId);

    /**
     * 根据用户id获取发件箱
     * @param visitorId
     * @return java.util.List<cn.edu.bistu.model.entity.Message>
     * */
    ServiceResult<JSONObject> getSendMessageById(Page<MessageVo> page, Long visitorId);

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

    /**
     * 修改消息，仅用于上传附件
     * @param message
     * @return void
     * */
    void updateMessage(Message message);

    void deleteMessage(Message message, boolean isSender);
}
