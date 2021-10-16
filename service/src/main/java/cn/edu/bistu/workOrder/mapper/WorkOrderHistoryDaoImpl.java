package cn.edu.bistu.workOrder.mapper;

import cn.edu.bistu.common.utils.Pagination;
import cn.edu.bistu.model.common.result.DaoResult;
import cn.edu.bistu.model.common.result.SimpleDaoResultImpl;
import cn.edu.bistu.model.entity.WorkOrderHistory;
import cn.edu.bistu.model.vo.WorkOrderHistoryVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class WorkOrderHistoryDaoImpl implements WorkOrderHistoryDao {
    @Autowired
    WorkOrderHistoryMapper workOrderHistoryMapper;

    @Override
    public DaoResult<Page<WorkOrderHistoryVo>> getWorkOrderHistoryPageByConditions(Page<WorkOrderHistoryVo> page, WorkOrderHistoryVo workOrderHistoryVo) {
        List<WorkOrderHistoryVo> workOrderHistoryList = workOrderHistoryMapper.getWorkOrderHistoryPageByConditions(Pagination.getSkip(page), page.getSize(), workOrderHistoryVo);
        page.setRecords(workOrderHistoryList);
        long workOrderHistoryCount = workOrderHistoryMapper.getWorkOrderHistoryCountByConditions(workOrderHistoryVo);
        page.setTotal(workOrderHistoryCount);
        return new SimpleDaoResultImpl<Page<WorkOrderHistoryVo>>().setResult(page);
    }

    @Override
    public DaoResult<WorkOrderHistoryVo> getOneWorkOrderHistoryById(Long id) {
        WorkOrderHistoryVo oneWorkOrderHistoryById = workOrderHistoryMapper.getOneWorkOrderHistoryById(id);
        return new SimpleDaoResultImpl<WorkOrderHistoryVo>().setResult(oneWorkOrderHistoryById);
    }

    @Override
    public void insertOne(WorkOrderHistory workOrderHistory) {
        workOrderHistoryMapper.insert(workOrderHistory);
    }
}
