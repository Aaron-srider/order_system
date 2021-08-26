package cn.edu.bistu.model.entity.auth;

import cn.edu.bistu.model.entity.BaseEntity;
import lombok.Data;

@Data
public class Permission extends BaseEntity {
    /**
     * 权限名称
     */
    String name;

    /**
     * 权限类型
     */
    String type;

    /**
     * 权限url
     */
    String url;

    /**
     * 授权代码
     */
    String percode;

    /**
     * 权限父节点id
     */
    Long parentId;

    /**
     * 权限父节点序列
     */
    String parentids;

    /**
     * 权限排序
     */
    Integer order;
}
