package cn.edu.bistu.model.vo;

import cn.edu.bistu.model.entity.auth.User;
import lombok.Data;


/**
 * 与前端交互的对象
 */
@Data
public class UserVo extends User {
    
    private String token;
    private String collegeName;
    private String majorName;
    private String className;
    private String secondaryDeptName;

}
