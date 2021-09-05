package cn.edu.bistu.model.wx;

import lombok.Data;

/**
 * 服务器接收微信服务器通知消息的对象
 */
@Data
public class JsonMiniTemplateMessageNotification {

    private String toUserName;
    private String fromUserName;
    private String createTime;
    private String msgType;
    private String event;
    List list;
}
