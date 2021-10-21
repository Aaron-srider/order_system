package cn.edu.bistu.model.entity.auth;

import cn.edu.bistu.constants.Role;
import cn.edu.bistu.model.common.validation.WhenStudent;
import cn.edu.bistu.model.common.validation.WhenTeacher;
import cn.edu.bistu.model.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.Date;


/**
 * 与数据库交互的对象
 */
@Data
public class User extends BaseEntity {
    @TableField("open_id")
    private String openId;

    @TableField("union_id")
    private String unionId;

    @TableField("session_key")
    private String sessionKey;

    @NotNull(groups = {WhenTeacher.class})
    @NotNull(groups = {WhenStudent.class})
    private String name;

    @TableField("info_complete")
    private Integer infoComplete;

    //wx信息
    private Integer gender;

    @TableField("avatar_url")
    private String avatarUrl;

    @TableField("nick_name")
    private String nickName;

    @TableField("college_id")
    @NotNull(groups = {WhenStudent.class, WhenTeacher.class})
    private Integer collegeId;

    //学生属性
    @TableField("major_id")
    @NotNull(groups = {WhenStudent.class})
    @Null(groups = {WhenTeacher.class})
    private Integer majorId;

    @TableField("clazz_name")
    @NotNull(groups = {WhenStudent.class})
    @Null(groups = {WhenTeacher.class})
    private String clazzName;

    @NotNull(groups = {WhenStudent.class})
    private Integer grade;

    //教师领导属性
    @TableField("secondary_dept_id")
    @NotNull(groups = {WhenTeacher.class})
    @Null(groups = {WhenStudent.class})
    private Long secondaryDeptId;

    @TableField(fill= FieldFill.INSERT)
    private Integer isLock;

    @TableField("student_job_id")
    @NotNull(groups = {WhenTeacher.class})
    @NotNull(groups = {WhenStudent.class})
    private String StudentJobId;


    //导师id
    @TableField("tutor_id")
    private Long tutorId;

}
