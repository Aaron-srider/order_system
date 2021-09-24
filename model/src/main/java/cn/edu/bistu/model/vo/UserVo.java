package cn.edu.bistu.model.vo;

import cn.edu.bistu.model.common.validation.ConditionQuery;
import cn.edu.bistu.model.common.validation.CustomSequenceProvider;
import cn.edu.bistu.model.common.validation.WhenStudent;
import cn.edu.bistu.model.common.validation.WhenTeacher;
import cn.edu.bistu.model.entity.auth.User;
import lombok.Data;
import org.hibernate.validator.group.GroupSequenceProvider;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;


/**
 * 与前端交互的对象，校验规则：对roleId进行分类校验：
 *      必传参数：
 *              * roleId
 *              * id
 *              * collegeName
 *              * name
 *      如果roleId判断出是student：
 *          非空：
 *              * majorName
 *              * clazzName
 *              * studentId
 *              * grade
 *
 *      如果roleId判断出是teacher：
 *          非空：
 *              * secondaryDeptName
 *              * jobId
 *
 */
@Data
@GroupSequenceProvider(CustomSequenceProvider.class)
public class UserVo extends User {
    
    private String token;

    private String collegeName;

    private String majorName;


    private String secondaryDeptName;

    @NotNull
    private Long roleId;

    @NotNull(groups = {ConditionQuery.class})
    private String roleCategory;

}
