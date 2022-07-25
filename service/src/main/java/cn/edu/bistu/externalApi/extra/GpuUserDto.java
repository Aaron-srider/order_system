package cn.edu.bistu.externalApi.extra;

import lombok.Data;

@Data
public class GpuUserDto {
    private Long id;

    /**
     * 用户名，用于登录所申请gpu资源。支持中文
     */
    private String username;

    /**
     * 学号或工号
     */
    private String studentJobId;

    /**
     * 相关资源申请描述
     */
    private String remark;

    /**
     * 申请资源类型
     */
    private Integer resourceType;

    private Integer applyDays;

    private String businessType;

}
