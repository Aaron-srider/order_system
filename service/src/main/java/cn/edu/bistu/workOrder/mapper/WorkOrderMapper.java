package cn.edu.bistu.workOrder.mapper;

import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.model.vo.WorkOrderVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface WorkOrderMapper extends BaseMapper<WorkOrder>{

    /**
     * 工单真删除
     * @param workOrderId 待删除的工单的工单id
     */
    public void deleteWorkOrderByWorkOrderId(Long workOrderId);

}
