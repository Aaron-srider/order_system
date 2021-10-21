package cn.edu.bistu.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.hibernate.validator.spi.group.DefaultGroupSequenceProvider;

import javax.validation.Constraint;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author: Mr.Yu
 * @create: 2021-09-08 22:27
 **/
@Data
public class Message {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long sender;
    @NotNull
    private Long receiver;
    private Integer type;

    private byte[] attachment;
    @TableField(value = "create_time")
    private Date createTime;
    private Integer status;

    @NotNull
    private String title;
    @NotNull
    private String description;
    @TableField(value = "attachment_name")
    private String attachmentName;

    @TableField(value = "is_show_sender")
    private Integer isShowSender;
    @TableField(value = "is_show_receiver")
    private Integer isShowReceiver;

}
