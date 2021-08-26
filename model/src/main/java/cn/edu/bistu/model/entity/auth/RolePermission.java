package cn.edu.bistu.model.entity.auth;


import cn.edu.bistu.model.entity.BaseEntity;
import lombok.Data;

@Data
public class RolePermission extends BaseEntity {
    Long roleId;
    Long permissionId;
}
