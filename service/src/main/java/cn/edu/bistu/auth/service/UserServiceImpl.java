package cn.edu.bistu.auth.service;

import cn.edu.bistu.auth.mapper.UserMapper;
import cn.edu.bistu.auth.mapper.UserRoleMapper;
import cn.edu.bistu.common.exception.UserNotRegisteredException;
import cn.edu.bistu.constants.ResultCodeEnum;
import cn.edu.bistu.model.entity.auth.User;
import cn.edu.bistu.model.entity.auth.UserRole;
import cn.edu.bistu.model.vo.UserVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    UserRoleMapper userRoleMapper;

    @Override
    public void userInfoCompletion(UserVo userVo, Long roleId) {

        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("id", userVo.getId());
        User user = userMapper.selectOne(wrapper);

        //用户没注册
        if (user == null) {
            throw new UserNotRegisteredException("user id: " + userVo.getId(), ResultCodeEnum.USER_NOT_REGISTERED);
        }

        //用户已经完善过信息了
        if (user.isUserInfoCompleted()) {
            throw new UserNotRegisteredException("user id: " + userVo.getId(), ResultCodeEnum.USER_INFO_COMPLETED);
        }

        //更新用户表
        userMapper.userInfoComplete(userVo);

        //向UserRole表中插入数据
        UserRole userRole = new UserRole();
        userRole.setRoleId(roleId);
        userRole.setUserId(userVo.getId());
        userRoleMapper.insert(userRole);
    }

}
