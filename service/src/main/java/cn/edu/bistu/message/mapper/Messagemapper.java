package cn.edu.bistu.message.mapper;

import cn.edu.bistu.model.entity.Message;
import cn.edu.bistu.model.vo.MessageVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author: Mr.Yu
 * @create: 2021-09-08 23:00
 **/
@Repository
public interface Messagemapper extends BaseMapper<Message> {

    /**
     * 获取收件箱
     * @return java.util.List<cn.edu.bistu.model.vo.MessageVo>
     * */
    List<MessageVo> getReceiveMessages(@Param("skip") long skip, @Param("size") long size,@Param("message") Message message);

    /**
     * 获取发件箱
     * @return java.util.List<cn.edu.bistu.model.vo.MessageVo>
     * */
    List<MessageVo> getSendMessages(@Param("skip") long skip, @Param("size") long size,@Param("message") Message message);

    /**
     * 获取消息的除附件的所有字段
     * @param messageId
     * @return cn.edu.bistu.model.entity.Message
     * */
    Message getMessageById(Long messageId);

    /**
     * 获取收件箱消息数量
     * @param
     * @return long
     * */
    long getReceiveMsgCount(@Param("message") Message message);
    /**
     * 获取发件箱消息数量
     * @param
     * @return long
     * */
    long getSendMsgCount(@Param("message") Message message);
}