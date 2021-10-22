package cn.edu.bistu.flow.mapper;

import cn.edu.bistu.model.entity.Flow;
import cn.edu.bistu.model.vo.FlowVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlowMapper extends BaseMapper<Flow>{

    public List<FlowVo> getAllFlowListByRoleId(@Param("roleId") long roleId);

    public FlowVo getFullPreparedFlowByFlowId(@Param("flowId") long flowId);

}
