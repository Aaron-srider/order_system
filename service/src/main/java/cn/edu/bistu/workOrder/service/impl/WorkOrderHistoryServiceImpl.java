package cn.edu.bistu.workOrder.service.impl;

import cn.edu.bistu.auth.mapper.UserMapper;
import cn.edu.bistu.flow.mapper.FlowMapper;
import cn.edu.bistu.flow.mapper.FlowNodeMapper;
import cn.edu.bistu.model.common.result.DaoResult;
import cn.edu.bistu.model.common.result.ServiceResult;
import cn.edu.bistu.model.common.result.ServiceResultImpl;
import cn.edu.bistu.model.entity.WorkOrderHistory;
import cn.edu.bistu.workOrder.mapper.WorkOrderDao;
import cn.edu.bistu.workOrder.mapper.WorkOrderHistoryMapper;
import cn.edu.bistu.workOrder.service.WorkOrderHistoryService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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

    //@Autowired
    //ContextPathConfiguration contextPathConfiguration;

    @Autowired
    UserMapper userMapper;

    @Autowired
    FlowMapper flowMapper;

    @Autowired
    FlowNodeMapper flowNodeMapper;

    @Autowired
    WorkOrderDao workOrderDao;

    @Override
    public Page<JSONObject> listWorkOrderHistory(WorkOrderHistory workOrderHistory, Page<WorkOrderHistory> page) throws NoSuchFieldException, IllegalAccessException {
        QueryWrapper<WorkOrderHistory> wrapper = new QueryWrapper<>();
        wrapper.like("title", workOrderHistory.getTitle());
        DaoResult<Page<JSONObject>> resultPage = workOrderDao.getWorkOrderHistoryPageByWrapper(page, wrapper);
        return resultPage.getResult();
    }

    @Override
    public ServiceResult<JSONObject> detail(WorkOrderHistory workOrderHistory) throws NoSuchFieldException, IllegalAccessException {
        QueryWrapper<WorkOrderHistory> wrapper = new QueryWrapper<>();
        wrapper.eq("id", workOrderHistory.getId());
        DaoResult<WorkOrderHistory> jsonObject = workOrderDao.getOneWorkOrderHistoryByWrapper(wrapper);
        ServiceResult<JSONObject> serviceResult = new ServiceResultImpl<>(jsonObject.getValue());
        return serviceResult;

    }


}
