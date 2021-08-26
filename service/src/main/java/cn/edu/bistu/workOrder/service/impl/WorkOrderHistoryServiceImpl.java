package cn.edu.bistu.workOrder.service.impl;

import cn.edu.bistu.common.config.ContextPathConfiguration;
import cn.edu.bistu.model.entity.WorkOrderHistory;
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

@Slf4j
@Service
public class WorkOrderHistoryServiceImpl extends ServiceImpl<WorkOrderHistoryMapper, WorkOrderHistory> implements WorkOrderHistoryService {

    @Autowired
    WorkOrderHistoryMapper workOrderHistoryMapper;

    @Value("${attachmentDownloadApi}")
    String attachmentDownloadApi;

    @Autowired
    ContextPathConfiguration contextPathConfiguration;

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

    
}
