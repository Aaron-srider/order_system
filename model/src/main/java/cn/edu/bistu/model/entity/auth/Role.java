package cn.edu.bistu.model.entity.auth;


import cn.edu.bistu.model.entity.BaseEntity;
import lombok.Data;

@Data
public class Role extends BaseEntity {
    /**
     * 角色名称
     */
    cn.edu.bistu.constants.Role name;
}
