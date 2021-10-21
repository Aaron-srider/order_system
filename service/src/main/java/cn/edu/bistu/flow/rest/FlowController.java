package cn.edu.bistu.flow.rest;

import cn.edu.bistu.flow.service.FlowService;
import cn.edu.bistu.model.common.result.Result;
import cn.edu.bistu.model.common.result.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@RestController
@CrossOrigin
@Validated
public class FlowController {

    @Autowired
    FlowService flowService;

    @GetMapping("/flows")
    public Result getFlowByRoleId(@NotNull Long roleId) {
        ServiceResult flowListByRoleId = flowService.getFlowListByRoleId(roleId);
        return new Result().ok(flowListByRoleId.getServiceResult());
    }
}
