package cn.edu.bistu.admin.user.dao;

import cn.edu.bistu.model.common.result.DaoResult;
import cn.edu.bistu.model.entity.auth.User;
import cn.edu.bistu.model.entity.auth.UserRole;
import cn.edu.bistu.model.vo.UserVo;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public interface UserDao {

    public DaoResult<JSONObject> getAllRoles();

    public DaoResult<UserVo> getOneUserById(Long id);

    public DaoResult<UserVo> getOneUserByOpenId(String openId);

    /**
     * 根据unionId查询用户
     * @return 如果用户注册过，无论用户是否完善信息，返回该用户信息；否则返回null
     */
    public DaoResult<UserVo> getOneUserByUnionId(String unionId);

    public DaoResult<Page<UserVo>> getUserListByConditions(Page<UserVo> page, UserVo userVo);

    public void simpleUpdateUserById(User user);

    public void demoteUserFromAdmin(Long userId);

    public void promoteUser2Admin(Long userId);

    public List<UserRole> getUserRoleByUserId(long userId);

    public void updateUserRoleByUserRoleId(UserRole userRole);

    public void insertUserRole(UserRole userRole);

    public void insertUser(User user);

    public DaoResult<List<UserVo>> getOneUserByStudentJobId(String studentJobId);
}
