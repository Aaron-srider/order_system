package cn.edu.bistu.workOrder.service.impl;

import cn.edu.bistu.common.BeanUtils;
import cn.edu.bistu.common.config.ContextPathConfiguration;
import cn.edu.bistu.constants.ResultCodeEnum;
import cn.edu.bistu.model.common.Result;
import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.model.entity.WorkOrderHistory;
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
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class WorkOrderServiceImpl extends ServiceImpl<WorkOrderMapper, WorkOrder> implements WorkOrderService {

    @Autowired
    WorkOrderMapper workOrderMapper;

    @Value("${attachmentDownloadApi}")
    String attachmentDownloadApi;

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


}
