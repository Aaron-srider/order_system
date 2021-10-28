package cn.edu.bistu.workOrder.service.impl;

import cn.edu.bistu.admin.User.mapper.UserDao;
import cn.edu.bistu.approval.WorkOrderFinisherFactory;
import cn.edu.bistu.approval.dao.ApproverLogicDao;
import cn.edu.bistu.approval.service.ApprovalService;
import cn.edu.bistu.common.MD5Utils;
import cn.edu.bistu.common.exception.ResultCodeException;
import cn.edu.bistu.constants.ResultCodeEnum;
import cn.edu.bistu.constants.WorkOrderStatus;
import cn.edu.bistu.flow.dao.FlowDaoImpl;
import cn.edu.bistu.flow.service.FlowNodeService;
import cn.edu.bistu.model.common.result.DaoResult;
import cn.edu.bistu.model.common.result.ServiceResult;
import cn.edu.bistu.model.common.result.ServiceResultImpl;
import cn.edu.bistu.model.entity.ApproverLogic;
import cn.edu.bistu.model.entity.FlowNode;
import cn.edu.bistu.model.entity.FlowNodeApprover;
import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.model.vo.FlowNodeVo;
import cn.edu.bistu.model.vo.FlowVo;
import cn.edu.bistu.model.vo.UserVo;
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
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${attachmentDownloadApi}")
    String attachmentDownloadApi;

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
    public ServiceResult<WorkOrderVo> detail(WorkOrder workOrder) {

        WorkOrder inspectWorkOrder = ((WorkOrderDaoImpl) workOrderDao).getWorkOrderMapper().selectOne(new QueryWrapper<WorkOrder>().select("id", "initiator_id").eq("id", workOrder.getId()));

        //工单不存在
        if (inspectWorkOrder == null) {
            throw new ResultCodeException("workOrder id: " + workOrder.getId(), ResultCodeEnum.WORKORDER_NOT_EXISTS);
        }

        //来访者不是工单的属主
        if (!inspectWorkOrder.getInitiatorId().equals(workOrder.getInitiatorId())) {
            throw new ResultCodeException("workOrder id: " + workOrder.getId(), ResultCodeEnum.HAVE_NO_RIGHT);
        }

        DaoResult<WorkOrderVo> daoResultPage = workOrderDao.getOneWorkOrderById(workOrder.getId());
        WorkOrderVo resultWorkOrderWithOutFlowInfo = daoResultPage.getResult();
        resultWorkOrderWithOutFlowInfo.setAttachment(null);


        //完善工单信息
        FlowVo fullPreparedFlowOfResultWorkOrder = flowDao.getFullPreparedFlowByFlowId(resultWorkOrderWithOutFlowInfo.getFlowId()).getResult();

        for (FlowNodeVo oneFlowNodeOfResultWorkOrder : fullPreparedFlowOfResultWorkOrder.getFlowNodeList()) {
            Long approverId = oneFlowNodeOfResultWorkOrder.getApproverId();
            flowNodeApproverDecider = flowNodeApproverDeciderFactory.getApproverDecider(approverId);
            FlowNodeApprover flowNodeApproverOfResultWorkOrder = flowNodeApproverDecider.findAndSetFlowNodeApprover(oneFlowNodeOfResultWorkOrder);
            oneFlowNodeOfResultWorkOrder.setFlowNodeApprover(flowNodeApproverOfResultWorkOrder);
        }

        resultWorkOrderWithOutFlowInfo.setFlow(fullPreparedFlowOfResultWorkOrder);

        if(resultWorkOrderWithOutFlowInfo.getAttachmentName() != null){
            //生成附件下载id
            String rowData = System.currentTimeMillis() + resultWorkOrderWithOutFlowInfo.getId() + resultWorkOrderWithOutFlowInfo.getAttachmentName();
            String md5Id = MD5Utils.MD5(rowData);
            resultWorkOrderWithOutFlowInfo.setAttachmentDownloadId(md5Id);
            WorkOrder workOrder1 = new WorkOrder();
            workOrder1.setId(resultWorkOrderWithOutFlowInfo.getId());
            workOrder1.setAttachmentDownloadId(md5Id);
            workOrderDao.updateById(workOrder1);
            return new ServiceResultImpl<>(resultWorkOrderWithOutFlowInfo);
        }

        return new ServiceResultImpl<>(resultWorkOrderWithOutFlowInfo);
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
        ((WorkOrderDaoImpl)workOrderDao).deleteWorkOrderAttachment(workOrderId);
    }

}
