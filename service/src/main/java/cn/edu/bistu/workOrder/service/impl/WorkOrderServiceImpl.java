package cn.edu.bistu.workOrder.service.impl;

import cn.edu.bistu.admin.user.dao.UserDao;
import cn.edu.bistu.approval.WorkOrderFinisherFactory;
import cn.edu.bistu.approval.dao.ApproverLogicDao;
import cn.edu.bistu.approval.service.ApprovalService;
import cn.edu.bistu.common.exception.ResultCodeException;
import cn.edu.bistu.constants.ResultCodeEnum;
import cn.edu.bistu.constants.WorkOrderStatus;
import cn.edu.bistu.flow.dao.FlowDaoImpl;
import cn.edu.bistu.flow.service.FlowNodeService;
import cn.edu.bistu.model.common.result.DaoResult;
import cn.edu.bistu.model.common.result.ServiceResult;
import cn.edu.bistu.model.common.result.ServiceResultImpl;
import cn.edu.bistu.model.entity.FlowNode;
import cn.edu.bistu.model.entity.FlowNodeApprover;
import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.model.vo.FlowNodeVo;
import cn.edu.bistu.model.vo.FlowVo;
import cn.edu.bistu.model.vo.WorkOrderVo;
import cn.edu.bistu.workOrder.dao.WorkOrderDao;
import cn.edu.bistu.workOrder.dao.WorkOrderDaoImpl;
import cn.edu.bistu.workOrder.mapper.WorkOrderMapper;
import cn.edu.bistu.workOrder.service.ActualApproverFinalizer;
import cn.edu.bistu.workOrder.service.FlowNodeApproverDecider;
import cn.edu.bistu.workOrder.service.FlowNodeApproverDeciderFactory;
import cn.edu.bistu.workOrder.service.WorkOrderService;
import cn.edu.bistu.wx.service.WxMiniApi;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class WorkOrderServiceImpl extends ServiceImpl<WorkOrderMapper, WorkOrder> implements WorkOrderService {
    @Autowired
    WxMiniApi wxMiniApi;

    @Qualifier("workOrderDaoImpl")
    @Autowired
    WorkOrderDao workOrderDao;

    @Autowired
    FlowDaoImpl flowDao;

    @Autowired
    FlowNodeService flowNodeService;

    @Autowired
    UserDao userDao;

    @Autowired
    ApprovalService approvalService;

    @Autowired
    WorkOrderFinisherFactory workOrderFinisherFactory;

    @Autowired
    ApproverLogicDao approverLogicDao;

    @Autowired
    ActualApproverFinalizer actualApproverFinalizer;

    @Autowired
    FlowNodeApproverDeciderFactory flowNodeApproverDeciderFactory;

    FlowNodeApproverDecider flowNodeApproverDecider;


    @Override
    public ServiceResult listWorkOrder(WorkOrderVo workOrderVo, Page<WorkOrderVo> page) {
        DaoResult<Page<WorkOrderVo>> daoResultPage = workOrderDao.getWorkOrderPageByConditions(page, workOrderVo, "user");
        return new ServiceResultImpl<>(daoResultPage.getResult());
    }

    @Override
    public void revoke(Long workOrderId, Long initiator) {

        WorkOrderVo workOrderVo = workOrderDao.getOneWorkOrderById(workOrderId).getResult();

        //“撤回接口”访问者与工单发起者不是同一个用户，无权操作
        if (!workOrderVo.getInitiatorId().equals(initiator)) {
            throw new ResultCodeException("user: " + initiator + " has no right",
                    ResultCodeEnum.HAVE_NO_RIGHT);
        }

        //工单已经结束，撤回操作非法
        if (workOrderVo.getIsFinished().equals(1)) {
            throw new ResultCodeException("workOrderId:" + workOrderId,
                    ResultCodeEnum.WORKORDER_BEEN_FINISHED);
        }

        //工单已经被审批过，撤回操作非法
        if (workOrderVo.getIsExamined().equals(1)) {
            throw new ResultCodeException("workOrderId:" + workOrderId,
                    ResultCodeEnum.WORKORDER_BEEN_EXAMINED);
        }

        approvalService.workOrderFinish(workOrderFinisherFactory.getFinisher(
                        "notApprovalTypeV2"),
                workOrderVo,
                null,
                WorkOrderStatus.BEEN_WITHDRAWN,
                null);
    }

    @Override
    public ServiceResult<WorkOrderVo> detail(Long workOrderId, Long visitorId) {

        WorkOrder dbWorkOrder = ((WorkOrderDaoImpl) workOrderDao).getWorkOrderMapper()
                        .selectOne(new QueryWrapper<WorkOrder>()
                        .select("id", "initiator_id")
                        .eq("id", workOrderId));

        //工单不存在
        if (dbWorkOrder == null) {
            log.info("工单号为：\""+workOrderId+"\"的工单不存在");
            throw new ResultCodeException("workOrder id: " + workOrderId, ResultCodeEnum.WORKORDER_NOT_EXISTS);
        }

        //来访者不是工单的属主也不是审批者，不予查看
        if (!dbWorkOrder.getInitiatorId().equals(visitorId)) {
            if (dbWorkOrder.getActualApproverId() != null
                    && !dbWorkOrder.getActualApproverId().equals(visitorId)) {
                log.info("来访者无权查看工单号为：\""+workOrderId+"\"的工单");
                throw new ResultCodeException("workOrder id: " + workOrderId, ResultCodeEnum.HAVE_NO_RIGHT);
            }
        }

        WorkOrderVo resultWorkOrderWithOutFlowInfo = workOrderDao.getOneWorkOrderById(workOrderId).getResult();
        resultWorkOrderWithOutFlowInfo.setAttachment(null);

        //完善工单的审批流程信息
        resultWorkOrderWithOutFlowInfo.setFlow(getFullPreparedFlowInfo(resultWorkOrderWithOutFlowInfo.getFlowId()));

        return new ServiceResultImpl<>(resultWorkOrderWithOutFlowInfo);
    }

    private FlowVo getFullPreparedFlowInfo(Long flowId) {
        FlowVo resultFlow = flowDao.getFullPreparedFlowByFlowId(flowId)
                .getResult();

        for (FlowNodeVo oneFlow : resultFlow.getFlowNodeList()) {
            decideFlowNodeApprover(oneFlow);
        }
        return resultFlow;
    }

    private void decideFlowNodeApprover(FlowNodeVo flowNodeVo) {
        Long approverId = flowNodeVo.getApproverId();
        flowNodeApproverDecider = flowNodeApproverDeciderFactory.getApproverDecider(approverId);
        FlowNodeApprover flowNodeApproverOfResultWorkOrder = flowNodeApproverDecider.findAndSetFlowNodeApprover(flowNodeVo);
        flowNodeVo.setFlowNodeApprover(flowNodeApproverOfResultWorkOrder);

    }

    @Override
    @Transactional
    public ServiceResult submitWorkOrder(WorkOrderVo workOrderVo) {

        Long flowId = workOrderVo.getFlowId();
        QueryWrapper<FlowNode> flowNodeQueryWrapper = new QueryWrapper<>();

        flowNodeQueryWrapper.eq("flow_id", flowId).orderByAsc("node_order");
        List<FlowNode> flowNodeList = flowDao.getFlowNodeMapper().selectList(flowNodeQueryWrapper);

        FlowNode firstFlowNode = flowNodeList.get(0);

        //设置生成的工单的状态
        workOrderVo.setFlowNodeId(firstFlowNode.getId());//目前所处流程节点

        //动态决定工单的实际审批者
        actualApproverFinalizer.decideActualApprover(workOrderVo, false);

        workOrderVo.setStatus(0);                           //工单状态
        workOrderVo.setIsExamined(0);                       //是否被审批过
        workOrderVo.setIsFinished(0);                       //是否完成
        //保存工单
        save(workOrderVo);

        return new ServiceResultImpl(workOrderVo);


        //通知审批者，这步暂时不动
        //UserVo userVo = userMapper.getOneById(workOrder.getId());
        //String openId = userVo.getOpenId();
        //wxMiniApi.sendSubscribeMsg(openId);
    }

    @Override
    public ServiceResult getAllWorkOrders(Page<WorkOrderVo> page, WorkOrderVo workOrderVo) {
        DaoResult<Page<WorkOrderVo>> workOrderPageByConditions = workOrderDao.getWorkOrderPageByConditions(page, workOrderVo, "admin");
        Page<WorkOrderVo> result = workOrderPageByConditions.getResult();
        return new ServiceResultImpl<>(result);
    }

    @Override
    public void deleteAttachmentByWorkOrderId(Long workOrderId) {
        ((WorkOrderDaoImpl) workOrderDao).deleteWorkOrderAttachment(workOrderId);
    }

}
