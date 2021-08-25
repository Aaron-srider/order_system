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
     * 返回workOrderVo的page
     * @return
     */
    Page<WorkOrderHistoryVo> workOrderHistoryPages(Page<WorkOrderHistory> page, @Param("workOrderHistoryVo") WorkOrderHistoryVo workOrderHistoryVo);


}
