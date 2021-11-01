package cn.edu.bistu.flow.dao;

import cn.edu.bistu.flow.mapper.FlowMapper;
import cn.edu.bistu.flow.mapper.FlowNodeMapper;
import cn.edu.bistu.model.common.result.DaoResult;
import cn.edu.bistu.model.common.result.DaoResultImpl;
import cn.edu.bistu.model.common.result.SimpleDaoResultImpl;
import cn.edu.bistu.model.entity.Flow;
import cn.edu.bistu.model.entity.FlowNode;
import cn.edu.bistu.model.vo.FlowVo;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Data
@Repository
public class FlowDaoImpl implements FlowDao{
    @Autowired
    FlowNodeMapper flowNodeMapper;
    @Autowired
    FlowMapper flowMapper;

    @Override
    public DaoResult getFlowListByRoleId(long roleId) {
        List<FlowVo> flowListByRoleId = flowMapper.getAllFlowListByRoleId(roleId);
        return new SimpleDaoResultImpl<>().setResult(flowListByRoleId);
    }

    @Override
    public DaoResult getOneFlowNodeByNodeId(Long flowNodeId) {
        return new SimpleDaoResultImpl<>().setResult(flowNodeMapper.selectById(flowNodeId));
    }

    @Override
    public DaoResult<FlowVo> getFullPreparedFlowByFlowId(Long flowId) {
        FlowVo fullPreparedFlowByFlowId = flowMapper.getFullPreparedFlowByFlowId(flowId);
        return new SimpleDaoResultImpl<FlowVo>().setResult(fullPreparedFlowByFlowId);
    }
}
