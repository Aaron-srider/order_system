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
     * @param id
     * @return java.util.List<cn.edu.bistu.model.vo.MessageVo>
     * */
    IPage<MessageVo> getReceiveMessageById(Page<MessageVo> page, @Param("id") Long id, @Param("title") String title);

    /**
     * 获取发件箱
     * @param id
     * @return java.util.List<cn.edu.bistu.model.vo.MessageVo>
     * */
    IPage<MessageVo> getSendMessageById(Page<MessageVo> page,@Param("id") Long id, @Param("title") String title);

    /**
     * 获取收取信息的详情
     * @param id
     * @return cn.edu.bistu.model.vo.MessageVo
     * */
    MessageVo getReceiveMsgAllDetail(Long id);

    /**
     * 获取发送信息的详情
     * @param id
     * @return cn.edu.bistu.model.vo.MessageVo
     * */
    MessageVo getSendMsgAllDetail(Long id);

    Message getMessageById(Long messageId);


}