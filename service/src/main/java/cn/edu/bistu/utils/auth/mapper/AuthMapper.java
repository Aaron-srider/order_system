package cn.edu.bistu.utils.auth.mapper;


import cn.edu.bistu.model.entity.auth.Permission;
import cn.edu.bistu.model.vo.UserVo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthMapper {

    /**
     * 根据openId查询用户
     * @param openId 用户openId
     * @return 用户信息
     */
    UserVo authenticateUserByOpenId(String openId);

    /**
     * 根据用户的id查询用户的权限
     * @param id 用户id
     * @return 用户权限集合
     */
    List<Permission> getUserPermissionByUserId(Long id);
}
