package cn.edu.bistu.workOrder.service.impl;

import cn.edu.bistu.admin.User.mapper.UserDao;
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
import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.model.vo.WorkOrderVo;
import cn.edu.bistu.workOrder.mapper.WorkOrderDao;
import cn.edu.bistu.workOrder.mapper.WorkOrderDaoImpl;
import cn.edu.bistu.workOrder.mapper.WorkOrderMapper;
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

    @Override
    public ServiceResult listWorkOrder(WorkOrderVo workOrderVo, Page<WorkOrderVo> page) {
        DaoResult<Page<WorkOrderVo>> daoResultPage = workOrderDao.getWorkOrderPageByConditions(page, workOrderVo);
        return new ServiceResultImpl<>(daoResultPage.getResult());
    }

    @Override
    public void revoke(Long workOrderId, Long initiator) {

        WorkOrder workOrder = ((WorkOrderDaoImpl)workOrderDao).getWorkOrderMapper().selectById(workOrderId);

        //“撤回接口”访问者与工单发起者不是同一个用户，无权操作
        if (!workOrder.getInitiatorId().equals(initiator)) {
            throw new ResultCodeException("user: " + initiator + " has no right",
                    ResultCodeEnum.HAVE_NO_RIGHT);
        }

        //工单已经结束，撤回操作非法
        if (workOrder.getIsFinished().equals(1)) {
            throw new ResultCodeException("workOrderId:" + workOrderId,
                    ResultCodeEnum.WORKORDER_BEEN_FINISHED);
        }

        //工单已经被审批过，撤回操作非法
        if (workOrder.getIsExamined().equals(1)) {
            throw new ResultCodeException("workOrderId:" + workOrderId,
                    ResultCodeEnum.WORKORDER_BEEN_EXAMINED);
        }

        approvalService.workOrderFinish(workOrder, null, WorkOrderStatus.BEEN_WITHDRAWN);
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
        WorkOrderVo result = daoResultPage.getResult();
        result.setAttachment(null);
        return new ServiceResultImpl<>(result);
    }

    @Override
    public void submitWorkOrder(WorkOrder workOrder) {

        QueryWrapper<FlowNode> flowNodeQueryWrapper = new QueryWrapper<>();
        Long flowId = workOrder.getFlowId();
        flowNodeQueryWrapper.eq("flow_id", flowId).orderByAsc("node_order");
        List<FlowNode> flowNodeList = flowDao.getFlowNodeMapper().selectList(flowNodeQueryWrapper);

        //设置生成的工单的状态
        workOrder.setFlowNodeId(flowNodeList.get(0).getId());//目前所处流程节点
        workOrder.setStatus(0);                           //工单状态
        workOrder.setIsExamined(0);                       //是否被审批过
        workOrder.setIsFinished(0);                       //是否完成
        //保存工单
        save(workOrder);

        //通知审批者，这步暂时不动
        //UserVo userVo = userMapper.getOneById(workOrder.getId());
        //String openId = userVo.getOpenId();
        //wxMiniApi.sendSubscribeMsg(openId);
    }

    @Override
    public ServiceResult getAllWorkOrders(Page<WorkOrderVo> page, WorkOrderVo workOrderVo) {
        DaoResult<Page<WorkOrderVo>> workOrderPageByConditions = workOrderDao.getWorkOrderPageByConditions(page, workOrderVo);
        Page<WorkOrderVo> result = workOrderPageByConditions.getResult();
        return new ServiceResultImpl<>(result);
    }

    @Override
    public void deleteAttachmentByWorkOrderId(Long workOrderId) {
        ((WorkOrderDaoImpl)workOrderDao).deleteWorkOrderAttachment(workOrderId);
    }

}
