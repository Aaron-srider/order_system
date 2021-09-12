package cn.edu.bistu.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;

/**
 * @author: Mr.Yu
 * @create: 2021-09-08 22:27
 **/
@Data
public class Message {

    private Integer id;
    @TableField(value = "user_id")
    private Integer user_id;
    private Integer sender;
    private Integer receiver;
    private Integer type;

    @TableField(fill = FieldFill.INSERT)
    private byte[] content;
    @TableField(value = "send_time",fill = FieldFill.INSERT)
    private Date sendTime;
    private Integer status;

    private String title;
    private String description;
    @TableField(value = "attchment_name")
    private String attchmentName;

}
