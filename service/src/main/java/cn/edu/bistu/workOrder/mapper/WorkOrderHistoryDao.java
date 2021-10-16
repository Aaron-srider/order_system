package cn.edu.bistu.workOrder.mapper;

import cn.edu.bistu.common.utils.Pagination;
import cn.edu.bistu.model.common.result.DaoResult;
import cn.edu.bistu.model.common.result.SimpleDaoResultImpl;
import cn.edu.bistu.model.entity.WorkOrderHistory;
import cn.edu.bistu.model.vo.WorkOrderHistoryVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface WorkOrderHistoryDao {
    /**
     * 根据筛选条件查询工单历史分页数据
     * @param page 分页信息
     * @param workOrderHistoryVo
     */
    public DaoResult<Page<WorkOrderHistoryVo>> getWorkOrderHistoryPageByConditions(Page<WorkOrderHistoryVo> page, WorkOrderHistoryVo workOrderHistoryVo);
    /**
     * 通过工单id获取工单历史信息
     */
    public DaoResult<WorkOrderHistoryVo> getOneWorkOrderHistoryById(Long id);


    public void insertOne(WorkOrderHistory workOrderHistory);

}
