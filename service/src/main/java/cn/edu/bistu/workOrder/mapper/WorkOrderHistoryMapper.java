package cn.edu.bistu.workOrder.mapper;

import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.model.entity.WorkOrderHistory;
import cn.edu.bistu.model.vo.WorkOrderHistoryVo;
import cn.edu.bistu.model.vo.WorkOrderVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface WorkOrderHistoryMapper extends BaseMapper<WorkOrderHistory>{

    /**
     * 工单历史真删除
     * @param workOrderId 根据workOrderId删除工单历史
     */
    public void deleteWorkOrderHistoryByWorkOrderId(Long workOrderId);

    /**
     * 获取一条工单历史的详情
     */
    public WorkOrderHistoryVo getOneWorkOrderHistoryById(long id);

    /**
     * 获取分页的工单历史数据
     * @param workOrderHistoryVo 过滤条件
     */
    public List<WorkOrderHistoryVo> getWorkOrderHistoryPageByConditions(@Param("skip") long skip, @Param("size") long size, @Param("workOrderHistoryVo") WorkOrderHistoryVo workOrderHistoryVo);

    /**
     * 获取分页的工单历史数据
     * @param workOrderHistoryVo 过滤条件
     */
    public long getWorkOrderHistoryCountByConditions(@Param("workOrderHistoryVo") WorkOrderHistoryVo workOrderHistoryVo);

}
