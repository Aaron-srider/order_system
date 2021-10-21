package cn.edu.bistu.auth.mapper;

import cn.edu.bistu.dept.mapper.DeptDao;
import cn.edu.bistu.model.entity.auth.Permission;
import cn.edu.bistu.model.entity.auth.Role;
import cn.edu.bistu.model.entity.auth.RolePermission;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
@Data
public class AuthDao {

    @Autowired
    RoleMapper roleMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    UserRoleMapper userRoleMapper;
    @Autowired
    DeptDao deptDao;

    @Autowired
    PermissionMapper permissionMapper;

    @Autowired
    RolePermissionMapper rolePermissionMapper;

    public void prepareAllApiPermission() {

        //管理员角色
        List<Permission> permissionList = permissionMapper.selectList(new QueryWrapper<Permission>().eq("type", "permission").select("id"));

        for (Permission permission : permissionList) {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setPermissionId(permission.getId());
            rolePermission.setRoleId(1L);
            rolePermissionMapper.insert(rolePermission);
        }

        //教师角色
        Long[] teacherPermissionList = {12L, 15L, 26L, 27L, 28L, 32L, 34L, 35L, 36L, 40L,
        30L, 31L, 33L};
        addPermission2Role(Arrays.asList(teacherPermissionList), 3L);
        addPermission2Role(Arrays.asList(teacherPermissionList), 4L);
        addPermission2Role(Arrays.asList(teacherPermissionList), 5L);

        //学生角色
        Long[] studentPermissionList = {12L, 15L, 26L, 27L, 28L, 32L, 34L, 35L, 36L, 40L};
        addPermission2Role(Arrays.asList(teacherPermissionList), 6L);
        addPermission2Role(Arrays.asList(teacherPermissionList), 7L);

    }

    public void addPermission2Role(List<Long> permissionIdList, Long roleId) {

        for (Long permissionId : permissionIdList) {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setPermissionId(permissionId);
            rolePermission.setRoleId(roleId);
            rolePermissionMapper.insert(rolePermission);
        }

    }
}
