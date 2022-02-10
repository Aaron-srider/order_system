package cn.edu.bistu.message.service;

import cn.edu.bistu.utils.auth.mapper.UserMapper;
import cn.edu.bistu.common.MD5Utils;
import cn.edu.bistu.common.utils.Pagination;
import cn.edu.bistu.message.mapper.Messagemapper;
import cn.edu.bistu.model.entity.Message;
import cn.edu.bistu.model.vo.MessageVo;
import cn.edu.bistu.model.vo.UserVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Autowired
    UserMapper userMapper;

    static Logger logger = LoggerFactory.getLogger(MessageService.class);

    @Override
    public IPage<MessageVo> getReceiveMessages(Page<MessageVo> page, Message message) {

        List<MessageVo> messages = messagemapper.getReceiveMessages(Pagination.getSkip(page),page.getSize(), message);
        long receiveMsgCount = messagemapper.getReceiveMsgCount(message);
        return retCommon(page,messages,receiveMsgCount);
    }

    @Override
    public IPage<MessageVo> getSendMessages(Page<MessageVo> page, Message message) {

        List<MessageVo> messages = messagemapper.getSendMessages(Pagination.getSkip(page),page.getSize(), message);
        long sendMsgCount = messagemapper.getSendMsgCount(message);
        return retCommon(page,messages,sendMsgCount);
    }

    public IPage<MessageVo> retCommon(IPage<MessageVo> page, List<MessageVo> messages, long msgCount){

        page.setRecords(messages);
        page.setTotal(msgCount);
        return page;
    }

    @Override
    public Long sendMessage(Message message) {

        message.setType(1);
        message.setStatus(0);

        messagemapper.insert(message);
        Long id = message.getId();
        logger.info("获取的messageId: " + id);
        return id;
    }

    @Override
    public MessageVo messageDetail(Long messageId, Long userId) {

        UserVo userVo = userMapper.getOneUserById(userId);
        MessageVo messageVo = (MessageVo) messagemapper.getMessageById(messageId);
        messageVo.setInitiator(userVo);
        //设置attachment_download_id
        setAttachmentDownloadId(messageVo);
        return messageVo;
    }

    private void setAttachmentDownloadId(Message message){
        //生成下载附件的attachment_download_id
        //由于一条消息可以被两个人看到，所以attachment_download_id需要保证只有一个
        if(message.getAttachmentName() != null && message.getAttachmentName().length() != 0
            && message.getAttachmentDownloadId() == null) {
            String rowData = System.currentTimeMillis() + message.getId() + message.getAttachmentName();
            String attachmentDownloadId = MD5Utils.MD5(rowData);
            logger.info("attachment_download_id: ",attachmentDownloadId);
            //设置并更新数据库
            message.setAttachmentDownloadId(attachmentDownloadId);
            messagemapper.updateById(message);
        }
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
        //由于不想每次查询的时候都把附件从数据库中查询出来，所以自定义了sql，查询除了attachment的其他所有字段
        Message message = messagemapper.getMessageById(messageId);
        return message;
    }

    @Override
    public Message getAttachment(Long messageId) {
        Message message = messagemapper.selectById(messageId);
        return message;
    }

}
