package cn.edu.bistu.message.service;

import cn.edu.bistu.message.mapper.Messagemapper;
import cn.edu.bistu.model.common.ServiceResult;
import cn.edu.bistu.model.common.ServiceResultImpl;
import cn.edu.bistu.model.entity.Message;
import cn.edu.bistu.model.vo.MessageVo;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


/**
 * @author: Mr.Yu
 * @create: 2021-09-11 19:48
 **/
@Service
@Slf4j
public class MessageServiceImpl implements MessageService{

    @Autowired
    Messagemapper messagemapper;

    static Logger logger = LoggerFactory.getLogger(MessageService.class);

    @Override
    public ServiceResult<JSONObject> getReceiveMessageById(Long id) {

        List<MessageVo> messages = messagemapper.getReceiveMessageById(id);
        logger.info("messages: ",messages);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("messages",messages);
        ServiceResult<JSONObject> result = new ServiceResultImpl<>(jsonObject);
        return result;
    }

    @Override
    public ServiceResult<JSONObject> getSendMessageById(Long id) {

        List<MessageVo> messages = messagemapper.getSendMessageById(id);
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
}
