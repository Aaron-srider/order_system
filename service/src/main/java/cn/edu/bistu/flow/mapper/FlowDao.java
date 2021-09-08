package cn.edu.bistu.flow.mapper;

import cn.edu.bistu.model.entity.Flow;
import cn.edu.bistu.model.entity.FlowNode;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Data
@Repository
public class FlowDao {
    @Autowired
    FlowNodeMapper flowNodeMapper;
    @Autowired
    FlowMapper flowMapper;

    /**
     * 获取一个指定的流程信息，包括流程的所有流程节点信息。
     * @param flowQueryWrapper
     * @return 如果流程为空，返回null；如果流程不为空，返回流程信息和流程所有节点信息。
     */
    public JSONObject getOneFlowByWrapper(QueryWrapper<Flow> flowQueryWrapper) {
        Flow flow = flowMapper.selectOne(flowQueryWrapper);

        //如果查询结果不为空
        if(flow != null) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("result", flow);

            Long flowId = flow.getId();

            //获取flowId
            QueryWrapper<FlowNode> flowNodeQueryWrapper = new QueryWrapper<>();
            flowNodeQueryWrapper.eq("flow_id", flowId);
            List<FlowNode> flowNodeList = flowNodeMapper.selectList(flowNodeQueryWrapper);
            jsonObject.put("flowNodeList", flowNodeList);
            return jsonObject;
        }
        return null;
    }

    ///**
    // * 根据条件查找一个审批节点信息，只包含节点的基本信息，不包括审批节点对应的审批者信息，
    // * 如果有需求，可以调用AuthDao接口获取User信息。
    // * @param flowNodeQueryWrapper 查询条件
    // * @return 返回查询得到的FlowNode节点
    // */
    //public JSONObject getOneFlowNodeByWrapper(QueryWrapper<FlowNode> flowNodeQueryWrapper) {
    //    FlowNode flowNode = flowNodeMapper.selectOne(flowNodeQueryWrapper);
    //    String s = JSONObject.toJSONString(flowNode);
    //    JSONObject jsonObject = JSONObject.parseObject(s);
    //    return jsonObject;
    //}
}
