package cn.edu.bistu.test.rest;

import cn.edu.bistu.model.common.result.DaoResult;
import cn.edu.bistu.model.common.result.Result;
import cn.edu.bistu.model.vo.WorkOrderHistoryVo;
import cn.edu.bistu.model.vo.WorkOrderVo;
import cn.edu.bistu.workOrder.dao.WorkOrderDao;
import cn.edu.bistu.workOrder.dao.WorkOrderHistoryDao;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@CrossOrigin
@RequestMapping("/test")
public class TestWorkOrderDao {

    @Qualifier("workOrderDaoImpl")
    @Autowired
    WorkOrderDao workOrderDao;

    @Autowired
    WorkOrderHistoryDao workOrderHistoryDao;

    @GetMapping("/testGetWorkOrderPageByConditions")
    public Result getWorkOrderPageByConditions() {
        Page<WorkOrderVo> workOrderVoPage = new Page<>();
        WorkOrderVo workOrderVo = new WorkOrderVo();
        workOrderVo.setId(4L);

        DaoResult<Page<WorkOrderVo>> workOrderPageByConditions = workOrderDao.getWorkOrderPageByConditions(workOrderVoPage, workOrderVo);
        Page<WorkOrderVo> result = workOrderPageByConditions.getResult();
        return Result.ok(result);
    }

    @GetMapping("/testGetWorkOrderHistoryPageByConditions")
    public Result getWorkOrderHistoryPageByConditions() {
        Page<WorkOrderHistoryVo> workOrderHistoryVoPage = new Page<>();
        WorkOrderHistoryVo workOrderHistoryVo = new WorkOrderHistoryVo();
        workOrderHistoryVo.setWorkOrderVo(new WorkOrderVo());
        workOrderHistoryVo.getWorkOrderVo().setTitle("申请");

        DaoResult<Page<WorkOrderHistoryVo>> workOrderPageByConditions =
                workOrderHistoryDao.getWorkOrderHistoryPageByConditions(workOrderHistoryVoPage, workOrderHistoryVo);
        Page<WorkOrderHistoryVo> result = workOrderPageByConditions.getResult();
        return Result.ok(result);
    }

    @GetMapping("/testGetOneWorkOrderById")
    public Result getOneWorkOrderById() {
        DaoResult<WorkOrderVo> oneWorkOrderById =
                workOrderDao.getOneWorkOrderById(4L);
        oneWorkOrderById.getResult().setAttachment(null);
        return Result.ok(oneWorkOrderById.getResult());
    }

    @GetMapping("/testGetOneWorkOrderHistoryById")
    public Result getOneWorkOrderHistoryById() {
        DaoResult<WorkOrderHistoryVo> oneWorkOrderById =
                workOrderHistoryDao.getOneWorkOrderHistoryById(4L);
        return Result.ok(oneWorkOrderById.getResult());
    }


}
