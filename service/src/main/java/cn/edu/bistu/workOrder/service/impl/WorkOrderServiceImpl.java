package cn.edu.bistu.workOrder.service.impl;

import cn.edu.bistu.auth.mapper.UserMapper;
import cn.edu.bistu.common.BeanUtils;
import cn.edu.bistu.common.MapService;
import cn.edu.bistu.common.config.ContextPathConfiguration;
import cn.edu.bistu.common.exception.WorkOrderBeenFinishedException;
import cn.edu.bistu.constants.ResultCodeEnum;
import cn.edu.bistu.flow.mapper.FlowMapper;
import cn.edu.bistu.flow.mapper.FlowNodeMapper;
import cn.edu.bistu.flow.service.FlowNodeService;
import cn.edu.bistu.model.common.Result;
import cn.edu.bistu.model.entity.Flow;
import cn.edu.bistu.model.entity.FlowNode;
import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.model.entity.WorkOrderHistory;
import cn.edu.bistu.model.entity.auth.User;
import cn.edu.bistu.model.vo.UserVo;
import cn.edu.bistu.model.vo.WorkOrderVo;
import cn.edu.bistu.workOrder.mapper.WorkOrderHistoryMapper;
import cn.edu.bistu.workOrder.mapper.WorkOrderMapper;
import cn.edu.bistu.workOrder.service.WorkOrderService;
import cn.edu.bistu.wx.service.WxMiniApi;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class WorkOrderServiceImpl extends ServiceImpl<WorkOrderMapper, WorkOrder> implements WorkOrderService {
    @Autowired
    WxMiniApi wxMiniApi;


    @Autowired
    WorkOrderMapper workOrderMapper;

    @Value("${attachmentDownloadApi}")
    String attachmentDownloadApi;

    @Autowired
    UserMapper userMapper;


    @Autowired
    FlowMapper flowMapper;

    @Autowired
    FlowNodeMapper flowNodeMapper;

    @Autowired
    FlowNodeService flowNodeService;

    @Autowired
    WorkOrderHistoryMapper workOrderHistoryMapper;

    @Autowired
    ContextPathConfiguration contextPathConfiguration;

    @Autowired
    WorkOrderService workOrderService;

    @Override
    public IPage<WorkOrderVo> listWorkOrder(WorkOrderVo workOrderVo) {

        Page<WorkOrder> page = new Page<>();
        if (workOrderVo.getSize() != null) {
            page.setSize(workOrderVo.getSize());
        }
        if (workOrderVo.getCurrent() != null) {
            page.setCurrent(workOrderVo.getCurrent());
        }

        Page<WorkOrderVo> resultPage = workOrderMapper.workOrderPages(page, workOrderVo);
        for (WorkOrderVo workOrder : resultPage.getRecords()) {
            String attachmentName = workOrder.getAttachmentName();
            if (!StringUtils.isEmpty(attachmentName)) {
                String url = contextPathConfiguration.getUrl() +
                        attachmentDownloadApi +
                        "/" + workOrder.getId();
                log.debug(url);
                workOrder.setAttachmentUrl(url);
            }
        }
        return resultPage;

    }

    @Override
    public void revoke(Long workOrderId, Long initiator) {

        WorkOrder workOrder = workOrderMapper.selectById(workOrderId);

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
        workOrderMapper.updateById(workOrder);

        //生成历史工单
        WorkOrderHistory workOrderHistory = new WorkOrderHistory();
        BeanUtils.copyProperties(workOrder, workOrderHistory);
        workOrderHistoryMapper.insert(workOrderHistory);
    }

    @Override
    public Result detail(WorkOrder workOrder) {

        Long initiatorId = workOrder.getInitiatorId();
        //获取发起者信息
        User user = userMapper.selectById(initiatorId);

        //不返回用户openId和sessionKey
        Map<String, Object> initiator = BeanUtils.bean2Map(user, new String[] {
                "openId",
                "sessionKey"
        });

        //不返回工单二进制附件
        Map<String, Object> workOrderMap = BeanUtils.bean2Map(workOrder, new String[]{
                "attachment"
        });

        //整合用户信息到工单信息中
        workOrderMap.put("initiator", initiator);

        //查询工单流程
        Flow flow = flowMapper.selectById(workOrder.getFlowId());

        Map<String, Object> flowMap = BeanUtils.bean2Map(flow, null);

        //查询工单所有节点
        List<FlowNode> flowNodeList = flowNodeMapper.selectListByFlowId(flow.getId());

        //整合节点到流程中
        flowMap.put("flowNodeList", flowNodeList);

        //获取工单当前节点
        FlowNode currentFlowNode = flowNodeMapper.selectById(workOrder.getFlowNodeId());

        //整合当前流程节点到流程中
        flowMap.put("currentFlowNode", currentFlowNode);

        //整合流程到工单中
        workOrderMap.put("flow", flowMap);

        //如果工单没有附件，attachmentName设置为null
        if(workOrder.getAttachment() == null && workOrder.getAttachmentName() == null ) {
            workOrderMap.put("attachmentName", null);
        }

        Result result = Result.ok(workOrderMap);
        return result;
    }

    @Override
    public void submitWorkOrder(WorkOrderVo workOrderVo) {

        //获取工单对应的所有流程节点
        Long flowId = workOrderVo.getFlowId();
        List<FlowNode> flowNodes = flowNodeService.getFlowNodeByFlowId(flowId);

        //设置生成的工单的状态
        workOrderVo.setFlowNodeId(flowNodes.get(0).getId());//目前所处流程节点
        workOrderVo.setStatus(0);                           //工单状态
        workOrderVo.setIsExamined(0);                       //是否被审批过
        workOrderVo.setIsFinished(0);                       //是否完成
        //保存工单
        workOrderService.save(workOrderVo);

        //通知审批者
        UserVo userVo = userMapper.selectById(workOrderVo.getId());
        String openId = userVo.getOpenId();
        wxMiniApi.sendSubscribeMsg(openId);
    }


}
