package cn.edu.bistu.flow.service;

import cn.edu.bistu.model.common.result.ServiceResult;
import cn.edu.bistu.model.entity.Flow;
import cn.edu.bistu.model.vo.FlowVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface FlowService extends IService<Flow> {

    /**
     * 获取角色能获取的所有工单申请流程
     * @param roleId 角色ID
     */
    public ServiceResult getFlowListByRoleId(long roleId);

}
