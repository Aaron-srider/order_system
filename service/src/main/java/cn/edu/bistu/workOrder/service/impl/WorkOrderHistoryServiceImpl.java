package cn.edu.bistu.workOrder.service.impl;

import cn.edu.bistu.auth.mapper.UserMapper;
import cn.edu.bistu.common.BeanUtils;
import cn.edu.bistu.common.config.ContextPathConfiguration;
import cn.edu.bistu.flow.mapper.FlowMapper;
import cn.edu.bistu.flow.mapper.FlowNodeMapper;
import cn.edu.bistu.model.common.Result;
import cn.edu.bistu.model.entity.Flow;
import cn.edu.bistu.model.entity.FlowNode;
import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.model.entity.WorkOrderHistory;
import cn.edu.bistu.model.entity.auth.User;
import cn.edu.bistu.model.vo.WorkOrderHistoryVo;
import cn.edu.bistu.workOrder.mapper.WorkOrderHistoryMapper;
import cn.edu.bistu.workOrder.service.WorkOrderHistoryService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class WorkOrderHistoryServiceImpl extends ServiceImpl<WorkOrderHistoryMapper, WorkOrderHistory> implements WorkOrderHistoryService {

    @Autowired
    WorkOrderHistoryMapper workOrderHistoryMapper;

    @Value("${attachmentDownloadApi}")
    String attachmentDownloadApi;

    @Autowired
    ContextPathConfiguration contextPathConfiguration;

    @Autowired
    UserMapper userMapper;

    @Autowired
    FlowMapper flowMapper;

    @Autowired
    FlowNodeMapper flowNodeMapper;

    @Override
    public IPage<WorkOrderHistoryVo> listWorkOrderHistory(WorkOrderHistoryVo workOrderHistoryVo) {

        Page<WorkOrderHistory> page = new Page<>();
        if (workOrderHistoryVo.getSize() != null) {
            page.setSize(workOrderHistoryVo.getSize());
        }
        if (workOrderHistoryVo.getCurrent() != null) {
            page.setCurrent(workOrderHistoryVo.getCurrent());
        }

        Page<WorkOrderHistoryVo> resultPage = workOrderHistoryMapper.workOrderHistoryPages(page, workOrderHistoryVo);
        for (WorkOrderHistoryVo workOrderHistory : resultPage.getRecords()) {
            String attachmentName = workOrderHistory.getAttachmentName();
            if (!StringUtils.isEmpty(attachmentName)) {
                String url = contextPathConfiguration.getUrl() +
                        attachmentDownloadApi +
                        "/" + workOrderHistoryVo.getId();
                log.debug(url);
                workOrderHistory.setAttachmentUrl(url);
            }
        }
        return resultPage;

    }

    @Override
    public Result detail(WorkOrderHistory workOrderHistory) {

        Long initiatorId = workOrderHistory.getInitiatorId();
        //获取发起者信息
        User user = userMapper.selectById(initiatorId);

        //不返回用户openId和sessionKey
        Map<String, Object> initiator = BeanUtils.bean2Map(user, new String[] {
                "openId",
                "sessionKey"
        });

        //不返回工单二进制附件
        Map<String, Object> workOrderHistoryMap = BeanUtils.bean2Map(workOrderHistory, new String[]{
                "attachment"
        });

        //整合用户信息到工单信息中
        workOrderHistoryMap.put("initiator", initiator);

        //查询工单流程
        Flow flow = flowMapper.selectById(workOrderHistory.getFlowId());

        Map<String, Object> flowMap = BeanUtils.bean2Map(flow, null);

        //查询工单所有节点
        List<FlowNode> flowNodeList = flowNodeMapper.selectListByFlowId(flow.getId());

        //整合节点到流程中
        flowMap.put("flowNodeList", flowNodeList);

        //获取工单当前节点
        FlowNode currentFlowNode = flowNodeMapper.selectById(workOrderHistory.getFlowNodeId());

        //整合当前流程节点到流程中
        flowMap.put("currentFlowNode", currentFlowNode);

        //整合流程到工单中
        workOrderHistoryMap.put("flow", flowMap);

        //如果工单没有附件，attachmentName设置为null
        if(workOrderHistory.getAttachment() == null && workOrderHistory.getAttachmentName() == null ) {
            workOrderHistoryMap.put("attachmentName", null);
        }

        Result result = Result.ok(workOrderHistoryMap);
        return result;
    }


}
