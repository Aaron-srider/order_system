package cn.edu.bistu.message.service;

import cn.edu.bistu.auth.mapper.UserMapper;
import cn.edu.bistu.message.mapper.Messagemapper;
import cn.edu.bistu.model.common.result.ServiceResult;
import cn.edu.bistu.model.common.result.ServiceResultImpl;
import cn.edu.bistu.model.entity.Message;
import cn.edu.bistu.model.vo.MessageVo;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author: Mr.Yu
 * @create: 2021-09-11 19:48
 **/
@Service
@Slf4j
public class MessageServiceImpl implements MessageService{

    @Autowired
    Messagemapper messagemapper;

    @Autowired
    UserMapper userMapper;

    static Logger logger = LoggerFactory.getLogger(MessageService.class);

    @Override
    public ServiceResult<JSONObject> getReceiveMessageById(Page<MessageVo> page, Long id, String title) {

        IPage<MessageVo> messages = messagemapper.getReceiveMessageById(page, id, title);

        logger.info("messags: ", messages);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("messages", messages);
        ServiceResult<JSONObject> result = new ServiceResultImpl<>(jsonObject);
        return result;
    }

    @Override
    public ServiceResult<JSONObject> getSendMessageById(Page<MessageVo> page,Long id, String title) {

        IPage<MessageVo> messages = messagemapper.getSendMessageById(page, id, title);
        logger.info("messages: ",messages);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("messages",messages);
        ServiceResult<JSONObject> result = new ServiceResultImpl<>(jsonObject);
        return result;
    }

    @Override
    public Long sendMessageById(Message message) {

        message.setType(1);
        message.setStatus(0);
        message.setCreateTime(new Date());

        messagemapper.insert(message);
        Long id = message.getId();
        logger.info("获取的messageId: " + id);
        return id;
    }

    @Override
    public MessageVo getReceiveMessageDetail(Long messageId) {

        MessageVo messageVo = messagemapper.getReceiveMsgAllDetail(messageId);
        return messageVo;
    }

    @Override
    public MessageVo getSendMessageDetail(Long messageId) {

        MessageVo messageVo = messagemapper.getSendMsgAllDetail(messageId);
        return messageVo;
    }

    @Override
    public void updateMessage(Message message) {
        messagemapper.updateById(message);
    }

    @Override
    public void deleteMessage(Message message, boolean isSender) {

        if (isSender) {
            message.setIsShowSender(1);
        } else {
            message.setIsShowReceiver(1);
        }
        messagemapper.updateById(message);
    }

    @Override
    public Message getMessageById(Long messageId) {
        Message message = messagemapper.selectById(messageId);
        return message;
    }
}
