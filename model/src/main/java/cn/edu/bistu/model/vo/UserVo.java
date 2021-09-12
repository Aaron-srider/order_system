package cn.edu.bistu.model.vo;

import cn.edu.bistu.model.common.validation.CustomSequenceProvider;
import cn.edu.bistu.model.common.validation.WhenStudent;
import cn.edu.bistu.model.common.validation.WhenTeacher;
import cn.edu.bistu.model.entity.auth.User;
import jdk.nashorn.internal.objects.annotations.Where;
import lombok.Data;
import org.hibernate.validator.group.GroupSequenceProvider;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;


/**
 * 与前端交互的对象
 */
@Data
@GroupSequenceProvider(CustomSequenceProvider.class)
public class UserVo extends User {
    
    private String token;

    @NotNull(groups = {WhenStudent.class, WhenTeacher.class})
    private String collegeName;

    @NotNull(groups = {WhenStudent.class})
    @Null(groups = {WhenTeacher.class})
    private String majorName;

    @NotNull(groups = {WhenStudent.class})
    @Null(groups = {WhenTeacher.class})
    private String className;

    @NotNull(groups = {WhenTeacher.class})
    @Null(groups = {WhenStudent.class})
    private String secondaryDeptName;

    @NotNull
    private Long roleId;

}
