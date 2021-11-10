package cn.edu.bistu.workOrder.dao;

import cn.edu.bistu.user.dao.UserDao;
import cn.edu.bistu.common.utils.Pagination;
import cn.edu.bistu.flow.dao.FlowDaoImpl;
import cn.edu.bistu.model.common.result.DaoResult;
import cn.edu.bistu.model.common.result.DaoResultImpl;
import cn.edu.bistu.model.common.result.SimpleDaoResultImpl;
import cn.edu.bistu.model.entity.*;
import cn.edu.bistu.model.vo.FlowVo;
import cn.edu.bistu.model.vo.WorkOrderVo;
import cn.edu.bistu.workOrder.mapper.WorkOrderHistoryMapper;
import cn.edu.bistu.workOrder.mapper.WorkOrderMapper;
import cn.edu.bistu.workOrder.mapper.WorkOrderStatusMapper;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 不需要获取完整数据可以直接委托mapper的查询，如果需要定制查询，需要在dao中增加接口
 * ，并在接口中委托mapper查询数据，并组装返回。
 */
@Data
@Slf4j
@Repository
public class WorkOrderDaoImpl implements WorkOrderDao {
    @Autowired
    WorkOrderStatusMapper workOrderStatusMapper;

    @Autowired
    UserDao userDao;

    @Autowired
    FlowDaoImpl flowDao;

    @Autowired
    WorkOrderMapper workOrderMapper;

    @Autowired
    WorkOrderHistoryMapper workOrderHistoryMapper;

    public DaoResult<JSONObject> getAllWorkOrderStatus() {
        List<WorkOrderStatus> workOrderStatusList = workOrderStatusMapper.selectList(null);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("workOrderStatusList", workOrderStatusList);
        DaoResult<JSONObject> objectDaoResult = new DaoResultImpl<>();
        objectDaoResult.setResult(jsonObject);
        return objectDaoResult;
    }

    @Override
    public DaoResult<Page<WorkOrderVo>> getWorkOrderPageByConditions(Page<WorkOrderVo> page, WorkOrderVo workOrderVo, String condition) {
        List<WorkOrderVo> workOrderList = workOrderMapper.getWorkOrderPageByConditions(Pagination.getSkip(page), page.getSize(), workOrderVo, condition);
        page.setRecords(workOrderList);
        long workOrderCount = workOrderMapper.getWorkOrderCountByConditions(workOrderVo, condition);
        page.setTotal(workOrderCount);
        return new SimpleDaoResultImpl<Page<WorkOrderVo>>().setResult(page);
    }

    /**
     * 返回指定工单，基于方法getOneWorkOrderByWrapper进行封装，指定查询条件为id。
     *
     * @param id 工单id
     *
     * @return 返回指定工单
     */
    @Override
    public DaoResult<WorkOrderVo> getOneWorkOrderById(Long id) {
        WorkOrderVo resultWorkOrderVo = workOrderMapper.getOneWorkOrderById(id);
        if (resultWorkOrderVo.getFlowId() != null) {
            FlowNode flowNodeInfo = (FlowNode) flowDao.getOneFlowNodeByNodeId(resultWorkOrderVo.getFlowNodeId()).getResult();
            resultWorkOrderVo.setFlowNode(flowNodeInfo);
        }
        if (resultWorkOrderVo.getFlowId() != null) {
            FlowVo fullPreparedFlow = flowDao.getFullPreparedFlowByFlowId(resultWorkOrderVo.getFlowId()).getResult();
            resultWorkOrderVo.setFlow(fullPreparedFlow);
        }
        return new SimpleDaoResultImpl<WorkOrderVo>().setResult(resultWorkOrderVo);
    }

    @Override
    public DaoResult<Page<WorkOrderVo>> getApprovalWorkOrderPage(Page<WorkOrderVo> page, Long approverId, WorkOrderVo workOrderVo) {
        List<WorkOrderVo> workOrderVoList = workOrderMapper.getApprovalWorkOrderPageByApproverId(Pagination.getSkip(page), page.getSize(), approverId, workOrderVo);
        page.setRecords(workOrderVoList);
        long workOrderCount = workOrderMapper.getApprovalWorkOrderPageCountByApproverId(approverId);
        page.setTotal(workOrderCount);
        return new SimpleDaoResultImpl<Page<WorkOrderVo>>().setResult(page);
    }

    @Override
    public void updateById(WorkOrder workOrder) {
        workOrderMapper.updateById(workOrder);
    }


    public void deleteWorkOrderAttachment(Long workOrderId) {

        workOrderMapper.update(null, new UpdateWrapper<WorkOrder>()
                .set("attachment", null)
                .set("attachment_name", null)
                .set("attachment_size", null)
                .set("attachment_download_id", null)
                .eq("id", workOrderId));

    }

    @Override
    public WorkOrderStatus constantToEntity(cn.edu.bistu.constants.WorkOrderStatus statusConstant) {
        List<WorkOrderStatus> workOrderStatusesFromDateBase = workOrderStatusMapper.selectList(null);
        for (WorkOrderStatus statusFromDataBase : workOrderStatusesFromDateBase) {
            if (statusFromDataBase.getAlias().equals(statusConstant.toString())) {
                return statusFromDataBase;
            }
        }
        return null;
    }


}
