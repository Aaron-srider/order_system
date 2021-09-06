package cn.edu.bistu.auth.mapper;

import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.model.entity.auth.User;
import cn.edu.bistu.model.vo.UserVo;
import cn.edu.bistu.model.vo.WorkOrderVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper extends BaseMapper<User> {

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
     */
    Integer userInfoComplete(UserVo userVo);

    /**
     * 连接查询多张表，获取用户的详细信息:
     * u.id                         ：用户id
     * u.name,                      ：用户名称
     * u.open_id,                   ：用户open_id
     * r.name role,                 ：角色名称
     * u.create_time,               ：创建时间
     * u.update_time,               ：修改时间
     * col.name collegeName         ：学院名称
     *
     * 》教师专有
     * job_id,                      ：工号
     * se.name secondaryDeptName,   ：二级部门名称
     *
     * 》学生专有
     * student_id,                  ：学号
     * ma.name majorName,           ：专业名称
     * cl.name className,           ：班级名称
     * grade,                       ：年级号
     *
     * 》微信信息
     * avatar_url,                  ：头像地址
     * gender,                      ：性别
     * nick_name                    ：微信昵称
     *
     * @param id 用户id
     * @return 返回UserVo对象
     */
    UserVo getOneById(Long id);

    Page<WorkOrderVo> getApprovalWorkOrders(@Param("approverId") Long approverId, @Param("workOrderVo") WorkOrderVo workOrderVo);
}
