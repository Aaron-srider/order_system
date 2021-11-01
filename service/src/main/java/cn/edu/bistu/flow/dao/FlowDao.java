package cn.edu.bistu.flow.dao;

import cn.edu.bistu.model.common.result.DaoResult;
import cn.edu.bistu.model.entity.FlowNode;
import cn.edu.bistu.model.vo.FlowVo;


public interface FlowDao {

    /**
     * 查询对应角色能获取的所有流程
     * @param roleId
     */
    public DaoResult getFlowListByRoleId(long roleId);

    public DaoResult getOneFlowNodeByNodeId(Long flowNodeId);

    public DaoResult<FlowVo> getFullPreparedFlowByFlowId(Long flowId);


}
