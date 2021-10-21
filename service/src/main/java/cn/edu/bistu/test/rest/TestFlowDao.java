package cn.edu.bistu.test.rest;


import cn.edu.bistu.model.common.result.DaoResult;
import cn.edu.bistu.model.common.result.Result;
import cn.edu.bistu.model.vo.WorkOrderVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@CrossOrigin
@RequestMapping("/test")
public class TestFlowDao {


    //@GetMapping("/testGetWorkOrderPageByConditions")
    //public Result getWorkOrderPageByConditions() {
    //    Page<WorkOrderVo> workOrderVoPage = new Page<>();
    //    WorkOrderVo workOrderVo = new WorkOrderVo();
    //    workOrderVo.setId(4L);
    //
    //    DaoResult<Page<WorkOrderVo>> workOrderPageByConditions = workOrderDao.getWorkOrderPageByConditions(workOrderVoPage, workOrderVo);
    //    Page<WorkOrderVo> result = workOrderPageByConditions.getResult();
    //    return Result.ok(result);
    //}

}
