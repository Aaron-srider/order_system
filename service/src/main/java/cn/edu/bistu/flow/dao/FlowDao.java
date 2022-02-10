package cn.edu.bistu.flow.dao;

import cn.edu.bistu.model.common.result.DaoResult;
import cn.edu.bistu.model.entity.FlowNode;
import cn.edu.bistu.model.vo.FlowVo;


public interface FlowDao {

    /**
     * 查询对应角色能获取的所有流程
     * @param roleId 角色ID
     */
    public DaoResult getFlowListByRoleId(long roleId);

    /**
     * 根据流程节点id获取一个流程节点
     * @param flowNodeId 流程节点ID
     */
    public DaoResult getOneFlowNodeByNodeId(Long flowNodeId);

    /**
     * 根据流程id获取流程（包括流程的完整信息，比如流程节点）
     * @param flowId 流程ID
     */
    public DaoResult<FlowVo> getFullPreparedFlowByFlowId(Long flowId);


}
