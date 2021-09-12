package cn.edu.bistu.workOrder.service.impl;

import cn.edu.bistu.common.BeanUtils;
import cn.edu.bistu.common.config.ContextPathConfiguration;
import cn.edu.bistu.common.exception.WorkOrderBeenFinishedException;
import cn.edu.bistu.constants.ResultCodeEnum;
import cn.edu.bistu.flow.mapper.FlowDao;
import cn.edu.bistu.flow.service.FlowNodeService;
import cn.edu.bistu.model.common.DaoResult;
import cn.edu.bistu.model.common.ServiceResult;
import cn.edu.bistu.model.common.ServiceResultImpl;
import cn.edu.bistu.model.entity.FlowNode;
import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.model.entity.WorkOrderHistory;
import cn.edu.bistu.model.vo.WorkOrderVo;
import cn.edu.bistu.workOrder.mapper.WorkOrderDao;
import cn.edu.bistu.workOrder.mapper.WorkOrderMapper;
import cn.edu.bistu.workOrder.service.WorkOrderService;
import cn.edu.bistu.wx.service.WxMiniApi;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class WorkOrderServiceImpl extends ServiceImpl<WorkOrderMapper, WorkOrder> implements WorkOrderService {
    @Autowired
    WxMiniApi wxMiniApi;

    @Autowired
    WorkOrderDao workOrderDao;

    @Autowired
    FlowDao flowDao;

    @Value("${attachmentDownloadApi}")
    String attachmentDownloadApi;

    @Autowired
    FlowNodeService flowNodeService;

    @Autowired
    ContextPathConfiguration contextPathConfiguration;

    @Override
    public ServiceResult<JSONObject> listWorkOrder(WorkOrder workOrderVo, Page<WorkOrder> page) throws NoSuchFieldException, IllegalAccessException {
        QueryWrapper<WorkOrder> wrapper = new QueryWrapper<>();
        wrapper.like("title", workOrderVo.getTitle());
        DaoResult<Page<JSONObject>> daoResultPage = workOrderDao.getWorkOrderPageByWrapper(page,wrapper);
        JSONObject value = daoResultPage.getValue();

        return new ServiceResultImpl<JSONObject>(value);
    }

    @Override
    public void revoke(Long workOrderId, Long initiator) {

        WorkOrder workOrder = workOrderDao.getWorkOrderMapper().selectById(workOrderId);

        //“撤回接口”访问者与工单发起者不是同一个用户，无权操作
        if (!workOrder.getInitiatorId().equals(initiator)) {
            throw new WorkOrderBeenFinishedException("id " + initiator + " has no right",
                    ResultCodeEnum.HAVE_NO_RIGHT);
        }

        //工单已经结束，撤回操作非法
        if(workOrder.getIsFinished().equals(1)) {
            throw new WorkOrderBeenFinishedException("workOrderId:" + workOrderId,
                    ResultCodeEnum.WORKORDER_BEEN_FINISHED);
        }

        //工单已经被审批过，撤回操作非法
        if (workOrder.getIsExamined().equals(1)) {
            throw new WorkOrderBeenFinishedException("workOrderId:" + workOrderId,
                    ResultCodeEnum.WORKORDER_BEEN_EXAMINED);
        }

        //更新工单状态
        workOrder.setIsFinished(1);
        workOrder.setStatus(3);
        workOrderDao.getWorkOrderMapper().updateById(workOrder);

        //生成历史工单
        WorkOrderHistory workOrderHistory = new WorkOrderHistory();
        BeanUtils.copyProperties(workOrder, workOrderHistory);
        workOrderHistory.setWorkOrderId(workOrderId);
        workOrderDao.getWorkOrderHistoryMapper().insert(workOrderHistory);
    }

    @Override
    public  ServiceResult<JSONObject> detail(WorkOrder workOrder) throws NoSuchFieldException, IllegalAccessException {
        QueryWrapper<WorkOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("id", workOrder.getId());
        DaoResult<WorkOrder> daoResultPage = workOrderDao.getOneWorkOrderByWrapper(wrapper);
        new ServiceResultImpl<>(daoResultPage.getValue());
        return new ServiceResultImpl<>(daoResultPage.getValue());
    }

    @Override
    public void submitWorkOrder(WorkOrder workOrder) {

        QueryWrapper<FlowNode> flowNodeQueryWrapper= new QueryWrapper<>();
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

}
