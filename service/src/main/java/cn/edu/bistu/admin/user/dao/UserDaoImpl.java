package cn.edu.bistu.admin.user.dao;

import cn.edu.bistu.utils.auth.mapper.RoleMapper;
import cn.edu.bistu.utils.auth.mapper.UserMapper;
import cn.edu.bistu.utils.auth.mapper.UserRoleMapper;
import cn.edu.bistu.dept.mapper.DeptDao;
import cn.edu.bistu.model.common.result.DaoResult;
import cn.edu.bistu.model.common.result.DaoResultImpl;
import cn.edu.bistu.model.common.result.SimpleDaoResultImpl;
import cn.edu.bistu.model.entity.auth.Role;
import cn.edu.bistu.model.entity.auth.User;
import cn.edu.bistu.model.entity.auth.UserRole;
import cn.edu.bistu.model.vo.UserVo;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Data
public class UserDaoImpl implements UserDao {

    @Autowired
    RoleMapper roleMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    UserRoleMapper userRoleMapper;
    @Autowired
    DeptDao deptDao;

    public void promoteUser2Admin(Long userId) {
        UserRole userRole = new UserRole();
        userRole.setRoleId(1L);
        userRole.setUserId(userId);
        userRoleMapper.insert(userRole);
    }

    @Override
    public List<UserRole> getUserRoleByUserId(long userId) {
        List<UserRole> userRoleList = userRoleMapper.selectList(new QueryWrapper<UserRole>().eq("user_id", userId));
        return userRoleList;
    }

    @Override
    public void updateUserRoleByUserRoleId(UserRole userRole) {
        userRoleMapper.updateById(userRole);
    }

    @Override
    public void insertUserRole(UserRole userRole) {
        userRoleMapper.insert(userRole);
    }

    @Override
    public void insertUser(User user) {
        userMapper.insert(user);
    }

    @Override
    public DaoResult<List<UserVo>> getOneUserByStudentJobId(String studentJobId) {
        List<UserVo> resultUserVo = userMapper.getOneUserByStudentJobId(studentJobId);
        return new SimpleDaoResultImpl<List<UserVo>>().setResult(resultUserVo);
    }

    public void demoteUserFromAdmin(Long userId) {
        userRoleMapper.deleteAdminUserRoleByRoleId(userId);
    }

    public DaoResult<JSONObject> getAllRoles() {
        List<Role> roles = roleMapper.selectList(null);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("roleList", roles);

        DaoResult<JSONObject> objectDaoResult = new DaoResultImpl<>();
        objectDaoResult.setResult(jsonObject);
        return objectDaoResult;
    }

    @Override
    public DaoResult<UserVo> getOneUserById(Long id) {
        UserVo oneUserById = userMapper.getOneUserById(id);
        return new SimpleDaoResultImpl<UserVo>().setResult(oneUserById);
    }

    public DaoResult<UserVo> getOneUserByOpenId(String openId) {
        UserVo oneUserById = userMapper.getOneUserByOpenId(openId);
        return new SimpleDaoResultImpl<UserVo>().setResult(oneUserById);
    }

    /**
     * 根据unionId查询用户
     *
     * @return 如果用户注册过，无论用户是否完善信息，返回该用户信息；否则返回null
     */
    public DaoResult<UserVo> getOneUserByUnionId(String unionId) {
        UserVo oneUserById = userMapper.getOneUserByUnionId(unionId);
        return new SimpleDaoResultImpl<UserVo>().setResult(oneUserById);
    }

    @Override
    public DaoResult<Page<UserVo>> getUserListByConditions(Page<UserVo> page, UserVo userVo) {
        long skip = page.getSize() * (page.getCurrent() - 1);
        List<UserVo> userListByConditions = userMapper.getUserListByConditions(skip, page.getSize(), userVo);
        long total = userMapper.getUserCountByConditions(userVo);
        page.setTotal(total);

        page.setRecords(userListByConditions);
        return new SimpleDaoResultImpl<Page<UserVo>>().setResult(page);
    }

    @Override
    public void simpleUpdateUserById(User user) {
        userMapper.updateById(user);
    }



}
