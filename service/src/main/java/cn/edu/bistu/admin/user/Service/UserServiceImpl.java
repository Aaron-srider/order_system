package cn.edu.bistu.admin.user.Service;

import cn.edu.bistu.admin.user.dao.UserDao;
import cn.edu.bistu.utils.auth.mapper.UserMapper;
import cn.edu.bistu.common.exception.ResultCodeException;
import cn.edu.bistu.common.utils.UserUtils;
import cn.edu.bistu.constants.ResultCodeEnum;
import cn.edu.bistu.dept.mapper.DeptDao;
import cn.edu.bistu.model.common.result.DaoResult;
import cn.edu.bistu.model.common.result.ServiceResult;
import cn.edu.bistu.model.common.result.ServiceResultImpl;
import cn.edu.bistu.model.entity.auth.Role;
import cn.edu.bistu.model.entity.auth.User;
import cn.edu.bistu.model.vo.UserVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    UserUtils userUtils;

    @Autowired
    UserDao userDao;

    @Autowired
    DeptDao deptDao;

    @Override
    public ServiceResult getAllUsers(Page<UserVo> page, UserVo userVo) {
        DaoResult<Page<UserVo>> userListByConditions = userDao.getUserListByConditions(page, userVo);
        return new ServiceResultImpl(userListByConditions.getResult());
    }

    @Override
    public void lock(User user) {
        userDao.simpleUpdateUserById(user);
    }

    @Override
    public ServiceResult<UserVo> updateUser(UserVo userVo) {
        //更新用户
        userDao.simpleUpdateUserById(userVo);

        //返回更新后的用户
        DaoResult<UserVo> updatedUser = userDao.getOneUserById(userVo.getId());
        return new ServiceResultImpl<>(updatedUser.getResult());
    }

    @Override
    public boolean isAdmin(Long id) {
        DaoResult<UserVo> oneUserById = userDao.getOneUserById(id);
        List<Role> roleList = oneUserById.getResult().getRoleList();

        log.debug("admin: " + cn.edu.bistu.constants.Role.ADMIN.toString());
        log.debug("operator: " + cn.edu.bistu.constants.Role.OPERATOR.toString());

        for (Role role : roleList) {
            log.debug("roleName: " + role.getName());
            if (role.getName().equals(cn.edu.bistu.constants.Role.ADMIN.toString()) ||
                    role.getName().equals(cn.edu.bistu.constants.Role.OPERATOR.toString())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void promote(Long userId) {
        //检查用户是否已经是管理员
        DaoResult<UserVo> oneUserById = userDao.getOneUserById(userId);

        List<Role> roleList = oneUserById.getResult().getRoleList();
        for (Role role : roleList) {
            if (role.equals(userUtils.convertRoleConstant2Entity(cn.edu.bistu.constants.Role.ADMIN))) {
                throw new ResultCodeException("user id: " + userId, ResultCodeEnum.USER_IS_ADMIN);
            }
        }

        userDao.promoteUser2Admin(userId);
    }

    @Override
    public void demote(Long userId) {
        userDao.demoteUserFromAdmin(userId);
    }

    @Override
    public ServiceResult searchOneUserByStudentJobId(String studentJobId) {
        return new ServiceResultImpl<>(userDao.getOneUserByStudentJobId(studentJobId).getResult());
    }

}
