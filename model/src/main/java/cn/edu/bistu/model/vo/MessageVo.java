package cn.edu.bistu.model.vo;

import cn.edu.bistu.model.entity.Message;
import lombok.Data;

/**
 * @author: Mr.Yu
 * @create: 2021-09-11 21:00
 **/
@Data
public class MessageVo extends Message {

    //消息拥有者
    private UserVo initiator;
}
