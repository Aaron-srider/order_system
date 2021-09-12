package cn.edu.bistu.model.entity;

import cn.edu.bistu.model.common.validation.Revoke;
import cn.edu.bistu.model.common.validation.WhenStudent;
import cn.edu.bistu.model.common.validation.WhenTeacher;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class BaseEntity {

    //公共属性
    @TableField("id")
    @TableId(type= IdType.AUTO)
    @NotNull(groups = {WhenTeacher.class})
    @NotNull(groups = {WhenStudent.class})
    @NotNull(groups = {Revoke.class})

    private Long id;

    @TableField(fill= FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @TableField(fill= FieldFill.UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @TableLogic
    @TableField(fill= FieldFill.INSERT)
    private Integer deleted;

}
