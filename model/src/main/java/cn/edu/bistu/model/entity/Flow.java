package cn.edu.bistu.model.entity;

import lombok.Data;

@Data
public class Flow extends BaseEntity{
    private String description;
    private String name;

    //本次更新新开字段
    private Integer ifUserAddInfo;
}
