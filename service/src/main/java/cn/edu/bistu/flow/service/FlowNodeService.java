package cn.edu.bistu.flow.service;

import cn.edu.bistu.model.entity.FlowNode;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface FlowNodeService  extends IService<FlowNode> {

    /**
     * 查询指定流程对应的所有结点（按nodeOrder升序排列）
     * @param flowId 流程id
     * @return 流程对应的所有结点
     */
    List<FlowNode> getFlowNodeByFlowId(Long flowId);

}
