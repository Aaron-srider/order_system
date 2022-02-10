package cn.edu.bistu.flow.service.impl;

import cn.edu.bistu.flow.dao.FlowDao;
import cn.edu.bistu.flow.mapper.FlowMapper;
import cn.edu.bistu.flow.service.FlowService;
import cn.edu.bistu.model.common.result.DaoResult;
import cn.edu.bistu.model.common.result.ServiceResult;
import cn.edu.bistu.model.common.result.ServiceResultImpl;
import cn.edu.bistu.model.entity.Flow;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FlowServiceImpl extends ServiceImpl<FlowMapper, Flow> implements FlowService  {

    @Autowired
    FlowDao flowDao;

    @Override
    public ServiceResult getFlowListByRoleId(long roleId) {
        DaoResult flowListByRoleId = flowDao.getFlowListByRoleId(roleId);
        return new ServiceResultImpl(flowListByRoleId.getResult());
    }
}
