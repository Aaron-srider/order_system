package cn.edu.bistu.approval.rest;

import cn.edu.bistu.approval.service.ApprovalRecordService;
import cn.edu.bistu.model.common.Result;
import cn.edu.bistu.model.entity.ApprovalRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApprovalController {

    @Autowired
    ApprovalRecordService approvalService;

    @PostMapping("/approval/pass")
    public Result  pass() {


        return Result.ok();
    }


}
