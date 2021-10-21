package cn.edu.bistu.model.entity.auth;


import cn.edu.bistu.model.entity.BaseEntity;
import lombok.Data;

@Data
public class Role extends BaseEntity {
    /**
     * 角色名称
     */
    String name;

    /**
     * 展示在前端的角色文本
     */
    String text;
}
