package cn.edu.bistu.flow.mapper;

import cn.edu.bistu.model.entity.FlowNode;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlowNodeMapper extends BaseMapper<FlowNode> {

    List<FlowNode> selectListByFlowId(Long flowId);

}