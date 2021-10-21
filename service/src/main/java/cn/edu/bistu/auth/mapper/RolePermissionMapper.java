package cn.edu.bistu.auth.mapper;

import cn.edu.bistu.model.entity.auth.Permission;
import cn.edu.bistu.model.entity.auth.Role;
import cn.edu.bistu.model.entity.auth.RolePermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface RolePermissionMapper extends BaseMapper<RolePermission> {
}
