package cn.edu.bistu.User.Service;

import cn.edu.bistu.model.common.result.ServiceResult;
import cn.edu.bistu.model.entity.auth.User;
import cn.edu.bistu.model.vo.UserVo;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface UserService {
    public ServiceResult<JSONObject> getAllUsers(Page<UserVo> page);

    public void lock(User user);
}
