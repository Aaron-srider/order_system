package cn.edu.bistu.workOrder.mapper;

import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.model.entity.WorkOrderHistory;
import cn.edu.bistu.model.vo.WorkOrderHistoryVo;
import cn.edu.bistu.model.vo.WorkOrderVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface WorkOrderHistoryMapper extends BaseMapper<WorkOrderHistory>{

    /**
     * 工单历史真删除
     * @param workOrderId 根据workOrderId删除工单历史
     */
    public void deleteWorkOrderHistoryByWorkOrderId(Long workOrderId);

}
