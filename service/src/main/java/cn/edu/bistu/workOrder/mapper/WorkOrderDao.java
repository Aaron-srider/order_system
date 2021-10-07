package cn.edu.bistu.workOrder.mapper;

import cn.edu.bistu.User.mapper.UserDao;
import cn.edu.bistu.common.BeanUtils;
import cn.edu.bistu.common.exception.ResultCodeException;
import cn.edu.bistu.common.utils.Pagination;
import cn.edu.bistu.constants.ResultCodeEnum;
import cn.edu.bistu.flow.mapper.FlowDao;
import cn.edu.bistu.model.common.JsonUtils;
import cn.edu.bistu.model.common.result.DaoResult;
import cn.edu.bistu.model.common.result.DaoResultImpl;
import cn.edu.bistu.model.entity.Flow;
import cn.edu.bistu.model.entity.FlowNode;
import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.model.entity.WorkOrderHistory;
import cn.edu.bistu.model.entity.auth.User;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 不需要获取完整数据可以直接委托mapper的查询，如果需要定制查询，需要在dao中增加接口
 * ，并在接口中委托mapper查询数据，并组装返回。
 */
@Data
@Slf4j
@Repository
public class WorkOrderDao {
    @Autowired
    WorkOrderStatusMapper workOrderStatusMapper;

    @Autowired
    UserDao userDao;

    @Autowired
    FlowDao flowDao;

    @Autowired
    WorkOrderMapper workOrderMapper;

    @Autowired
    WorkOrderHistoryMapper workOrderHistoryMapper;

    /**
     * 获取工单分页数据，每个工单包括：工单发起者信息，工单对应的流程，工单当前所处流程节点。
     *
     * @param page    分页数据，有效参数：
     *                size：页大小
     *                current：页数
     * @param wrapper 工单查询条件
     * @return 返回工单分页数据，以及分页数据；如果不分页，返回的page对象的size，current，total置零，records仍然为有用数据。
     */
    public DaoResult<Page<JSONObject>> getWorkOrderPageByWrapper(Page<WorkOrder> page, QueryWrapper<WorkOrder> wrapper) throws NoSuchFieldException, IllegalAccessException {
        List<WorkOrder> workOrderList = workOrderMapper.selectList(wrapper);

        //用于封装工单结果
        List<JSONObject> resultList = new ArrayList<>();

        //获取所有工单的发起者信息、工单所有的节点信息
        for (WorkOrder workOrder : workOrderList) {
            JSONObject jsonObject = workOrderHandler(workOrder);
            resultList.add(jsonObject);
        }

        Page<JSONObject> page1 = Pagination.page(page, resultList);

        DaoResult<Page<JSONObject>> objectDaoResult = new DaoResultImpl<>();
        objectDaoResult.setResult(page1);

        return objectDaoResult;
    }

    /**
     * 获取工单分页数据，每个工单包括：工单发起者信息，工单对应的流程，工单当前所处流程节点。
     *
     * @param wrapper 工单查询条件
     * @return 返回工单分页数据，以及分页数据
     */
    public DaoResult<WorkOrder> getOneWorkOrderByWrapper(QueryWrapper<WorkOrder> wrapper) throws NoSuchFieldException, IllegalAccessException {
        WorkOrder workOrder = workOrderMapper.selectOne(wrapper);
        JSONObject jsonObject = workOrderHandler(workOrder);
        DaoResult<WorkOrder> objectDaoResult = new DaoResultImpl<>();
        objectDaoResult.setValue(jsonObject);
        return objectDaoResult;
    }

    /**
     * 返回指定工单，基于方法getOneWorkOrderByWrapper进行封装，指定查询条件为id。
     *
     * @param id 工单id
     * @return 返回指定工单
     */
    public DaoResult<WorkOrder> getOneWorkOrderById(Long id) throws NoSuchFieldException, IllegalAccessException {
        QueryWrapper<WorkOrder> workOrderQueryWrapper = new QueryWrapper<>();
        workOrderQueryWrapper.eq("id", id);
        return getOneWorkOrderByWrapper(workOrderQueryWrapper);
    }

    /**
     * 获取工单分页数据，每个工单包括：工单发起者信息，工单对应的流程，工单当前所处流程节点。
     *
     * @param wrapper 工单查询条件
     * @return 返回工单分页数据，以及分页数据
     */
    public DaoResult<WorkOrderHistory> getOneWorkOrderHistoryByWrapper(QueryWrapper<WorkOrderHistory> wrapper) throws NoSuchFieldException, IllegalAccessException {
        WorkOrderHistory WorkOrderHistory = workOrderHistoryMapper.selectOne(wrapper);
        JSONObject jsonObject = workOrderHandler(WorkOrderHistory);
        DaoResultImpl<WorkOrderHistory> objectDaoResult = new DaoResultImpl<>();
        objectDaoResult.setValue(jsonObject);
        return objectDaoResult;
    }


    /**
     * 获取工单分页数据，每个工单包括：工单发起者信息，工单对应的流程，工单当前所处流程节点。
     *
     * @param page    分页数据，包括每页数量，页数
     * @param wrapper 工单查询条件
     * @return 返回工单分页数据，以及分页数据
     */
    public DaoResult<Page<JSONObject>> getWorkOrderHistoryPageByWrapper(Page<WorkOrderHistory> page, QueryWrapper<WorkOrderHistory> wrapper) throws NoSuchFieldException, IllegalAccessException {
        List<WorkOrderHistory> workOrderHistoryList = workOrderHistoryMapper.selectList(wrapper);

        //用于封装工单结果
        List<JSONObject> resultList = new ArrayList<>();

        //获取所有工单的发起者信息、工单所有的节点信息
        for (WorkOrderHistory workOrderHistory : workOrderHistoryList) {
            JSONObject jsonObject = workOrderHandler(workOrderHistory);
            resultList.add(jsonObject);
        }

        Page<JSONObject> page1 = Pagination.page(page, resultList);

        DaoResult<Page<JSONObject>> objectDaoResult = new DaoResultImpl<>();
        objectDaoResult.setResult(page1);

        return objectDaoResult;
    }

    /**
     * 在workOrder或workOrderHistory被查询出来后，负责一些额外的装配工作：
     * 1.将attachment字段替换为空
     * 2.获取发起者实体信息
     * 3.获取流程实体信息
     * 4.获取当前流程节点信息
     *
     * @param object 待处理的workOrder或workOrderHistory
     * @return 返回Json对象
     * result：原始数据
     * initiator：发起者信息
     * flow：流程信息
     * currentFlowNode：当前流程节点信息
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    private <T> JSONObject workOrderHandler(Object object) throws NoSuchFieldException, IllegalAccessException {

        Class<?> aClass = object.getClass();

        Field attachmentField = BeanUtils.getDeclaredField(aClass, "attachment");
        attachmentField.setAccessible(true);
        attachmentField.set(object, null);

        Field initiatorIdField = BeanUtils.getDeclaredField(aClass, "initiatorId");
        initiatorIdField.setAccessible(true);
        Long initiatorId = (Long) initiatorIdField.get(object);

        Field flowIdField = BeanUtils.getDeclaredField(aClass, "flowId");
        flowIdField.setAccessible(true);
        Long flowId = (Long) flowIdField.get(object);

        Field flowNodeIdField = BeanUtils.getDeclaredField(aClass, "flowNodeId");
        flowNodeIdField.setAccessible(true);
        Long flowNodeId = (Long) flowNodeIdField.get(object);

        JSONObject detailInfo = improveWorkOrderInformation(initiatorId, flowId, flowNodeId);

        JSONObject oneWorkOrder = new JSONObject();

        JsonUtils.setResult(oneWorkOrder, object);
        JsonUtils.setDetailInfo(oneWorkOrder, detailInfo);

        return oneWorkOrder;
    }

    /**
     * 传入workOrder的一系列id，将其转化为对应的实体
     *
     * @return 数据结构如下：
     * {
     * "initiator":{xx}
     * "flow":{xx}
     * "currentFlowNode":{xx}
     * }
     */
    private JSONObject improveWorkOrderInformation(Long initiatorId, Long flowId, Long flowNodeId) {
        //获取发起者信息
        DaoResult<User> daoInitiator = userDao.getOneUserById(initiatorId);
        User initiator = daoInitiator.getResult();
        initiator.setOpenId(null);
        initiator.setSessionKey(null);

        //获取工单流程信息
        QueryWrapper<Flow> flowWrapper = new QueryWrapper<>();
        flowWrapper.eq("id", flowId);
        DaoResult<Flow> daoFlow = flowDao.getOneFlowByWrapper(flowWrapper);

        //获取工单目前所在流程节点信息
        FlowNode currentFlowNode = flowDao.getFlowNodeMapper().selectById(flowNodeId);

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("initiator", daoInitiator.getValue());
        jsonObject.put("flow", daoFlow.getValue());
        jsonObject.put("currentFlowNode", currentFlowNode);

        return jsonObject;
    }

    /**
     * 从审批节点表，工单表中查出指定审批者的待审批工单信息。
     * 主要委托getWorkOrderPageByWrapper接口获取工单数据。
     *
     * @param page        分页数据，包含以下有效数据:
     *                    size：要获取的页数大小
     *                    current：要获取的页数
     * @param approverId  审批者id
     * @param workOrder 包含有效数据:
     *                    title
     * @return
     */
    public DaoResult<Page<JSONObject>> getApprovalWorkOrderPage(Page<WorkOrder> page, Long approverId, WorkOrder workOrder) throws NoSuchFieldException, IllegalAccessException {

        QueryWrapper<FlowNode> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("approver_id", approverId);
        List<FlowNode> flowNodeList = flowDao.getFlowNodeMapper().selectList(queryWrapper);

        List<JSONObject> resultList = new ArrayList<>();
        for (FlowNode flowNode : flowNodeList) {
            Long flowNodeId = flowNode.getId();
            QueryWrapper<WorkOrder> workOrderQueryWrapper = new QueryWrapper<>();
            workOrderQueryWrapper.eq("flow_node_id", flowNodeId).like("title", workOrder.getTitle()).eq("is_finished", 0);
            DaoResult<Page<JSONObject>> daoWorkOrderPage = getWorkOrderPageByWrapper(page, workOrderQueryWrapper);
            Page<JSONObject> workOrderPage = daoWorkOrderPage.getResult();

            List<JSONObject> records = workOrderPage.getRecords();

            resultList.addAll(records);
        }

        Page<JSONObject> page1 = Pagination.page(page, resultList);

        DaoResult<Page<JSONObject>> pageDaoResult = new DaoResultImpl<>();

        pageDaoResult.setResult(page1);

        return pageDaoResult;
    }

    /**
     * 生成对应工单的历史记录
     * @param workOrder 工单
     */
    public void generateWorkOrderHistory(WorkOrder workOrder) {
        //生成历史工单
        WorkOrderHistory workOrderHistory = new WorkOrderHistory();
        BeanUtils.copyProperties(workOrder, workOrderHistory);
        workOrderHistory.setWorkOrderId(workOrder.getId());
        log.debug("workOrderHistory to be saved:" + workOrderHistory);
        workOrderHistoryMapper.insert(workOrderHistory);
    }


    public void deleteWorkOrderAttachment(Long workOrderId) {

        workOrderMapper.update(null, new UpdateWrapper<WorkOrder>()
                .set("attachment", null)
                .set("attachment_name", null)
                .set("attachment_size", null)
                .eq("id", workOrderId));

    }

}
