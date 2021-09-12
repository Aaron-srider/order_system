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
    private Integer collegeId;

    //学生属性
    @TableField("major_id")
    private Integer majorId;
    @TableField("class_id")
    private Integer classId;
    @NotNull(groups = {WhenStudent.class})
    private Integer grade;
    @TableField("student_id")

    @NotNull(groups = {WhenStudent.class})
    @Null(groups = {WhenTeacher.class})
    private String studentId;

    //教师领导属性
    @TableField("secondary_dept_id")
    private Long secondaryDeptId;

    @TableField("job_id")
    @NotNull(groups = {WhenTeacher.class})
    @Null(groups = {WhenStudent.class})
    private String jobId;



}
