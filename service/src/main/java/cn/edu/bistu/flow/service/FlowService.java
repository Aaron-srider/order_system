package cn.edu.bistu.flow.service;

import cn.edu.bistu.model.common.result.ServiceResult;
import cn.edu.bistu.model.entity.Flow;
import cn.edu.bistu.model.vo.FlowVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface FlowService extends IService<Flow> {

    /**
     * @param roleId
     */
    public ServiceResult getFlowListByRoleId(long roleId);

}
