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
     * 获取收件箱
     * @param visitorId
     * @return java.util.List<cn.edu.bistu.model.entity.Message>
     * */
    ServiceResult<JSONObject> getReceiveMessageById(Long visitorId);

    /**
     * 获取发件箱
     * @param visitorId
     * @return java.util.List<cn.edu.bistu.model.entity.Message>
     * */
    ServiceResult<JSONObject> getSendMessageById(Long visitorId);
}
