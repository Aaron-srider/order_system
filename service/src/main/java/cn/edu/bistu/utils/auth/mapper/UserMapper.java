package cn.edu.bistu.utils.auth.mapper;

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
     * r.name role,                 ：角色名称       连接查询
     * u.create_time,               ：创建时间
     * u.update_time,               ：修改时间
     * col.name collegeName         ：学院名称       连接查询
     *
     * 》教师专有
     * job_id,                      ：工号
     * se.name secondaryDeptName,   ：二级部门名称     连接查询
     *
     * 》学生专有
     * student_id,                  ：学号
     * ma.name majorName,           ：专业名称       连接查询
     * cl.name className,           ：班级名称       连接查询
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

    /**
     * 主要连接用户表、审批节点表、工单表，获取指定用户的待审批工单，工单信息如下：
     *
     * wo.id                :工单id
     * wo.is_examined,      ：工单是否被审批过
     * wo.flow_id,          ：工单流程id
     * wo.create_time,      ：工单创建时间
     * wo.update_time,      ：工单修改时间
     * wo.attachment_name,  ：工单附件名称
     * wo.status,           ：工单状态
     * wo.title,            ：工单标题
     * wo.content,          ：工单内容
     * initiator.student_id ,   ：发起者学号
     * initiator.job_id ,       ：发起者工号
     * initiator.name initiatorName,    ：发起者姓名
     * r.name role,                     ：发起者角色
     * f.name flowName                  ：工单流程名称
     *
     * @param page 分页数据，包含以下有效数据:
     *             size：要获取的页数大小
     *             current：要获取的页数
     * @param approverId 审批者id
     * @param workOrderVo 包含有效数据:
     *                     title
     * @return
     */
    Page<WorkOrderVo> getApprovalWorkOrders(Page<WorkOrderVo> page, @Param("approverId") Long approverId, @Param("workOrderVo") WorkOrderVo workOrderVo);

    List<Long> queryByCondition(UserVo userVo);



    //调整mapper策略
    UserVo getOneUserById(Long id);

    UserVo getOneUserByOpenId(String openId);

    List<UserVo> getOneUserByStudentJobId(String studentJobId);

    UserVo getOneUserByUnionId(String unionId);

    List<UserVo> getUserListByConditions(@Param("skip") Long skip, @Param("size")Long size, @Param("userVo")UserVo userVo);

    long getUserCountByConditions(@Param("userVo") UserVo userVo);
}
