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
    ServiceResult<JSONObject> getReceiveMessages(Page<MessageVo> page, Long visitorId, String title);

    /**
     * 根据用户id获取发件箱
     * @param visitorId
     * @return java.util.List<cn.edu.bistu.model.entity.Message>
     * */
    ServiceResult<JSONObject> getSendMessages(Page<MessageVo> page, Long visitorId, String title);

    /**
     * 发送工单请求，返回插入的工单id方便上传附件
     * @param message
     * @return java.lang.Integer
     * */
    Long sendMessage(Message message);

    /**
     * 获取消息详情
     * @param messageId
     * @param userId
     * @return cn.edu.bistu.model.vo.MessageVo
     * */
    MessageVo messageDetail(Long messageId, Long userId);

    /**
     * 修改消息，仅用于上传附件
     * @param message
     * @return void
     * */
    void updateMessage(Message message);

    /**
     * 假删除消息
     * @param message
     * @param isSender
     * @return void
     * */
    void deleteMessage(Message message, boolean isSender);

    /**
     * 根据id获取消息，不加载附件
     * @param messageId
     * @return cn.edu.bistu.model.entity.Message
     * */
    Message getMessageById(Long messageId);

    /**
     * 加载消息所有字段，下载附件时使用
     * @param messageId
     * @return cn.edu.bistu.model.entity.Message
     * */
    Message getAttachment(Long messageId);
}
