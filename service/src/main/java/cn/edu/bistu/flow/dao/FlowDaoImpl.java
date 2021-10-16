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

    /**
     * 返回指定审批节点的下一个节点id
     * @param flowNodeId 指定审批节点的id
     * @return 若指定节点没有下一个节点，返回null；否则返回下一个节点的id
     */
    public Long getNextFlowNode(Long flowNodeId) {
        FlowNode currentFlowNode = flowNodeMapper.selectById(flowNodeId);
        Long nextId = currentFlowNode.getNextId();
        return nextId;
    }

    /**
     * 获取一个指定的流程信息，包括流程的所有流程节点信息。
     *
     * @param flowQueryWrapper
     * @return 如果流程为空，返回null；如果流程不为空，返回流程信息和流程所有节点信息。
     */
    public DaoResult<Flow> getOneFlowByWrapper(QueryWrapper<Flow> flowQueryWrapper) {
        Flow flow = flowMapper.selectOne(flowQueryWrapper);

        //如果查询结果不为空
        if (flow != null) {

            DaoResult<Flow> daoResult = new DaoResultImpl<>();

            JSONObject jsonObject = new JSONObject();

            daoResult.setResult(flow);

            Long flowId = flow.getId();

            //获取flowId
            QueryWrapper<FlowNode> flowNodeQueryWrapper = new QueryWrapper<>();
            flowNodeQueryWrapper.eq("flow_id", flowId);
            List<FlowNode> flowNodeList = flowNodeMapper.selectList(flowNodeQueryWrapper);


            jsonObject.put("flowNodeList", flowNodeList);
            daoResult.setDetailInfo(jsonObject);

            return daoResult;
        }
        return null;
    }

    @Override
    public DaoResult getFlowListByRoleId(long roleId) {
        List<FlowVo> flowListByRoleId = flowMapper.getAllFlowListByRoleId(roleId);
        return new SimpleDaoResultImpl<>().setResult(flowListByRoleId);
    }
}
