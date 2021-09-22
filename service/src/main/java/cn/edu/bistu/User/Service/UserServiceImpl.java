package cn.edu.bistu.User.Service;

import cn.edu.bistu.User.mapper.UserDao;
import cn.edu.bistu.model.common.result.ServiceResult;
import cn.edu.bistu.model.common.result.ServiceResultImpl;
import cn.edu.bistu.model.entity.auth.User;
import cn.edu.bistu.model.vo.UserVo;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserDao userDao;

    @Override
    public ServiceResult<JSONObject> getAllUsers(Page<UserVo> page) {
        return new ServiceResultImpl<JSONObject>( userDao.getUserListByWrapper(page, null).getValue());
    }

    @Override
    public void lock(User user) {
        userDao.getUserMapper().updateById(user);
    }


}
