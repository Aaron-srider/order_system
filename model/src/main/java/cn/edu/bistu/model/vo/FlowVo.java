package cn.edu.bistu.model.vo;

import cn.edu.bistu.model.entity.Flow;
import cn.edu.bistu.model.entity.FlowNode;
import lombok.Data;

import java.util.List;

@Data
public class FlowVo extends Flow {
    List<FlowNodeVo> flowNodeList;
}
