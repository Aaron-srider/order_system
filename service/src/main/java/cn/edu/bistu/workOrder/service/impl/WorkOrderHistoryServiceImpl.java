package cn.edu.bistu.workOrder.service.impl;

import cn.edu.bistu.auth.mapper.UserMapper;
import cn.edu.bistu.flow.dao.FlowDao;
import cn.edu.bistu.flow.mapper.FlowMapper;
import cn.edu.bistu.flow.mapper.FlowNodeMapper;
import cn.edu.bistu.model.common.result.DaoResult;
import cn.edu.bistu.model.common.result.ServiceResult;
import cn.edu.bistu.model.common.result.ServiceResultImpl;
import cn.edu.bistu.model.entity.WorkOrderHistory;
import cn.edu.bistu.model.vo.FlowNodeVo;
import cn.edu.bistu.model.vo.FlowVo;
import cn.edu.bistu.model.vo.WorkOrderHistoryVo;
import cn.edu.bistu.model.vo.WorkOrderVo;
import cn.edu.bistu.workOrder.dao.WorkOrderHistoryDao;
import cn.edu.bistu.workOrder.mapper.WorkOrderHistoryMapper;
import cn.edu.bistu.workOrder.service.FlowNodeApproverDecider;
import cn.edu.bistu.workOrder.service.FlowNodeApproverDeciderFactory;
import cn.edu.bistu.workOrder.service.WorkOrderHistoryService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WorkOrderHistoryServiceImpl extends ServiceImpl<WorkOrderHistoryMapper, WorkOrderHistory> implements WorkOrderHistoryService {

    @Value("${attachmentDownloadApi}")
    String attachmentDownloadApi;

    @Autowired
    UserMapper userMapper;

    @Autowired
    FlowMapper flowMapper;

    @Autowired
    FlowNodeMapper flowNodeMapper;

    @Autowired
    WorkOrderHistoryDao workOrderHistoryDao;

    @Autowired
    FlowDao flowDao;

    FlowNodeApproverDecider flowNodeApproverDecider;

    @Autowired
    FlowNodeApproverDeciderFactory flowNodeApproverDeciderFactory;

    @Override
    public ServiceResult listWorkOrderHistory(WorkOrderHistoryVo workOrderHistoryVo, Page<WorkOrderHistoryVo> page) {
        DaoResult<Page<WorkOrderHistoryVo>> resultPage = workOrderHistoryDao.getWorkOrderHistoryPageByConditions(page, workOrderHistoryVo);
        return new ServiceResultImpl(resultPage.getResult());
    }

    @Override
    public ServiceResult<WorkOrderHistoryVo> detail(WorkOrderHistory workOrderHistory, long visitorId) {
        DaoResult<WorkOrderHistoryVo> daoResultPage = workOrderHistoryDao.getOneWorkOrderHistoryById(workOrderHistory.getId());
        WorkOrderHistoryVo resultWorkOrderHistory = daoResultPage.getResult();
        if(resultWorkOrderHistory!=null && resultWorkOrderHistory.getWorkOrderVo()!=null) {
            resultWorkOrderHistory.getWorkOrderVo().setAttachment(null);

            WorkOrderVo workOrderVoOfResultHistory = resultWorkOrderHistory.getWorkOrderVo();

            FlowVo fullPreparedFlowOfResultWorkOrder = flowDao.getFullPreparedFlowByFlowId(workOrderVoOfResultHistory.getFlowId()).getResult();

            for (FlowNodeVo oneFlowNodeOfResultWorkOrder : fullPreparedFlowOfResultWorkOrder.getFlowNodeList()) {
                Long approverId = oneFlowNodeOfResultWorkOrder.getApproverId();
                flowNodeApproverDecider = flowNodeApproverDeciderFactory.getApproverDecider(approverId);
                flowNodeApproverDecider.findAndSetFlowNodeApprover(oneFlowNodeOfResultWorkOrder);
            }
            workOrderVoOfResultHistory.setFlow(fullPreparedFlowOfResultWorkOrder);
        }
        
        return new ServiceResultImpl<>(resultWorkOrderHistory);
    }


}
