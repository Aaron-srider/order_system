package cn.edu.bistu.workOrder.service.impl;

import cn.edu.bistu.auth.mapper.UserMapper;
import cn.edu.bistu.common.BeanUtils;
import cn.edu.bistu.common.MapService;
import cn.edu.bistu.common.config.ContextPathConfiguration;
import cn.edu.bistu.constants.ResultCodeEnum;
import cn.edu.bistu.flow.mapper.FlowMapper;
import cn.edu.bistu.flow.mapper.FlowNodeMapper;
import cn.edu.bistu.model.common.Result;
import cn.edu.bistu.model.entity.Flow;
import cn.edu.bistu.model.entity.FlowNode;
import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.model.entity.WorkOrderHistory;
import cn.edu.bistu.model.entity.auth.User;
import cn.edu.bistu.model.vo.WorkOrderVo;
import cn.edu.bistu.workOrder.mapper.WorkOrderHistoryMapper;
import cn.edu.bistu.workOrder.mapper.WorkOrderMapper;
import cn.edu.bistu.workOrder.service.WorkOrderService;
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
    WorkOrderHistoryMapper workOrderHistoryMapper;

    @Autowired
    ContextPathConfiguration contextPathConfiguration;

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
    public Result revoke(Long workOrderId, Long initiator) {

        WorkOrder workOrder = workOrderMapper.selectById(workOrderId);

        if (!workOrder.getInitiatorId().equals(initiator)) {
            log.debug("id " + initiator + "：" + ResultCodeEnum.HAVE_NO_RIGHT.toString());
            return Result.build(null, ResultCodeEnum.HAVE_NO_RIGHT);
        }

        if (workOrder.getIsExamined().equals(1)) {
            log.debug("workOrderId " + workOrderId + "：" + ResultCodeEnum.WORKORDER_BEEN_EXAMINED.toString());
            return Result.build(null, ResultCodeEnum.WORKORDER_BEEN_EXAMINED);
        }

        workOrder.setIsFinished(1);
        workOrder.setStatus(3);

        workOrderMapper.updateById(workOrder);

        WorkOrderHistory workOrderHistory = new WorkOrderHistory();
        BeanUtils.copyProperties(workOrder, workOrderHistory);
        workOrderHistoryMapper.insert(workOrderHistory);

        return Result.ok();
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

        Map<String, Object> flowMap = BeanUtils.bean2Map(flow);

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


}
