package cn.edu.bistu.flow.service.impl;

import cn.edu.bistu.flow.mapper.FlowNodeMapper;
import cn.edu.bistu.flow.service.FlowNodeService;
import cn.edu.bistu.model.entity.FlowNode;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class FlowNodeServiceImpl extends ServiceImpl<FlowNodeMapper, FlowNode> implements FlowNodeService {


    @Autowired
    FlowNodeMapper flowNodeMapper;

    /**
     * 查询指定流程对应的所有结点（按nodeOrder升序排列）
     * @param flowId 流程id
     * @return 流程对应的所有结点
     */
    public List<FlowNode> getFlowNodeByFlowId(Long flowId) {
        QueryWrapper<FlowNode> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("flow_id", flowId).orderByAsc("node_order");

        List<FlowNode> flowNodes = flowNodeMapper.selectList(queryWrapper);

        return flowNodes;
    }

}