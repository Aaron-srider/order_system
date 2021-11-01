package cn.edu.bistu.model.entity;

import com.baomidou.mybatisplus.annotation.*;
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
@TableName("message")
public class Message {

    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    private Long sender;
    @NotNull
    private Long receiver;
    private Integer type;

    private byte[] attachment;
    @TableField(value = "create_time")
    private String createTime;
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
    @TableField(value = "attachment_download_id")
    private String attachmentDownloadId;

}
