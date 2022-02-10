package cn.edu.bistu.utils.auth.mapper;

import cn.edu.bistu.model.entity.auth.UserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleMapper extends BaseMapper<UserRole> {
    public void deleteAdminUserRoleByRoleId(Long userId);
}
