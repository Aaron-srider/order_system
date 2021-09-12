package cn.edu.bistu.message.mapper;

import cn.edu.bistu.model.entity.Message;
import cn.edu.bistu.model.vo.MessageVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author: Mr.Yu
 * @create: 2021-09-11 19:41
 **/
@Repository
public class MessageDao {

    @Autowired
    static Messagemapper mapper;


}
