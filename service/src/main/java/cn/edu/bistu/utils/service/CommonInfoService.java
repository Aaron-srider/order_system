package cn.edu.bistu.utils.service;


import cn.edu.bistu.model.common.result.ServiceResult;

public interface CommonInfoService {

    /**
     * 返回常用信息，包括：所有部门，所有学院，所有专业，所有工单状态，所有角色信息
     * @return
     */
    public ServiceResult getCommonInfo();
}
