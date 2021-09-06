package cn.edu.bistu.auth.service;

import cn.edu.bistu.model.common.Result;
import cn.edu.bistu.model.entity.auth.User;
import cn.edu.bistu.model.vo.UserVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface UserService extends IService<User> {

    /**
     * 提供给完善用户信息service使用
     * @param userVo 包含user的信息，信息如下:
     *               教师信息：
     *                  id,                     ：学生id
     *                  name,                   ：姓名
     *                  collegeName,            ：学院名称
     *                  secondaryDeptName,      ：所处二级部门名称
     *                  jobId                   ：工号（字符串）
     *
     *               学生信息：
     *                      id              ：学生id
     *                      name,           ：姓名
     *                      collegeName,    ：学院名称
     *                      majorName,      ：专业名称
     *                      className,      ：班级名称
     *                      grade,          ：年级号（整数）
     *                      studentId       ：学号（字符串）
     * @param roleId user的角色id，由于role与user是多对多的关系，所以需要将roleId和userId插入到关系user_role表中
     */
    void userInfoCompletion(UserVo userVo, Long roleId);

}
