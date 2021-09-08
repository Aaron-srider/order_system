package cn.edu.bistu.workOrder.service.impl;

import cn.edu.bistu.auth.mapper.UserMapper;
import cn.edu.bistu.common.BeanUtils;
import cn.edu.bistu.common.config.ContextPathConfiguration;
import cn.edu.bistu.flow.mapper.FlowMapper;
import cn.edu.bistu.flow.mapper.FlowNodeMapper;
import cn.edu.bistu.model.common.Result;
import cn.edu.bistu.model.entity.Flow;
import cn.edu.bistu.model.entity.FlowNode;
import cn.edu.bistu.model.entity.WorkOrderHistory;
import cn.edu.bistu.model.entity.auth.User;
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

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class WorkOrderHistoryServiceImpl extends ServiceImpl<WorkOrderHistoryMapper, WorkOrderHistory> implements WorkOrderHistoryService {

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

    @Autowired
    WorkOrderDao workOrderDao;

    @Override
    public Page<JSONObject> listWorkOrderHistory(WorkOrderHistory workOrderHistory, Page<WorkOrderHistory> page) throws NoSuchFieldException, IllegalAccessException {
        QueryWrapper<WorkOrderHistory> wrapper = new QueryWrapper<>();
        wrapper.like("title", workOrderHistory.getTitle());
        Page<JSONObject> resultPage = workOrderDao.getWorkOrderHistoryPageByWrapper(page, wrapper);
        return resultPage;
    }

    @Override
    public JSONObject detail(WorkOrderHistory workOrderHistory) throws NoSuchFieldException, IllegalAccessException {
        QueryWrapper<WorkOrderHistory> wrapper = new QueryWrapper<>();
        wrapper.eq("id", workOrderHistory.getId());
        JSONObject jsonObject = workOrderDao.getOneWorkOrderHistoryByWrapper(wrapper);
        return jsonObject;
    }


}
