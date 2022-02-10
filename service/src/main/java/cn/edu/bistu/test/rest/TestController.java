package cn.edu.bistu.test.rest;

import cn.edu.bistu.Entity;
import cn.edu.bistu.admin.user.dao.UserDao;
import cn.edu.bistu.utils.auth.mapper.AuthDao;
import cn.edu.bistu.flow.dao.FlowDao;
import cn.edu.bistu.model.common.result.Result;
import cn.edu.bistu.model.entity.FlowNode;
import cn.edu.bistu.model.vo.UserVo;
import cn.edu.bistu.model.vo.WorkOrderVo;
import cn.edu.bistu.test.Testtb;
import cn.edu.bistu.test.mapper.TestdbDao;
import cn.edu.bistu.workOrder.dao.WorkOrderDao;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


@RestController
@Validated
@CrossOrigin
@RequestMapping("/test")
public class TestController {

    @Autowired
    TestdbDao testdbDao;

    @Autowired
    UserDao userDao;

    @Autowired
    AuthDao authDao;

    @Qualifier("workOrderDaoImpl")
    @Autowired
    WorkOrderDao workOrderDao;

    @Autowired
    FlowDao flowDao;

    @GetMapping("/test1")
    public Result test1() {
       WorkOrderVo workOrderVo =
                workOrderDao.getOneWorkOrderById(1L).getResult();

        FlowNode flowNode = (FlowNode)flowDao.getOneFlowNodeByNodeId(workOrderVo.getFlowNodeId()).getResult();

        workOrderVo.setFlowNode(flowNode);
        return Result.ok(workOrderVo);
    }

    @GetMapping("/testNewGetAllUsers")
    public Result testNewGetAllUsers() {
        Page<UserVo> userVoPage = new Page<>();
        userVoPage.setSize(10).setCurrent(1);
        UserVo userVo = new UserVo();
        //userVo.setJobId("");
        userVo.setRoleCategory("all");
        userVo.setName("test");
        return Result.ok(userDao.getUserListByConditions(userVoPage, userVo).getResult());
    }

    @GetMapping("/testNewUserDao")
    public Result testNewUserDao() {
        return Result.ok(userDao.getOneUserById(1L).getResult());
    }

    @GetMapping("/prepareAllPermission")
    public void prepareAllPermission() {
        authDao.prepareAllApiPermission();
        return;
    }

    @PostMapping("/receiveListFromBody")
    public Entity testPost(@RequestBody Map<String, Object> map, HttpServletRequest req) {
        System.out.println(map);
        return null;
    }

    @GetMapping("/")
    public Entity testGet() {
        Entity entity = new Entity();
        entity.setAge("12");
        entity.setId("1");
        entity.setName("wec");
        return entity;
    }

    @Test
    public void test() {
        String[] arr = {"wsc", "cagsdfasdfc", "gsdfm", "xasc", "rasdfy"};
        Arrays.sort(arr, (String str1, String str2) -> {
            return str1.length() - str2.length();
        });
        System.out.println(Arrays.toString(arr));
    }

    @GetMapping("/testdb")
    public Result testdb() {
        JSONObject jsonObject = new JSONObject();
        List<Testtb> testtbs = testdbDao.selectList(null);
        return Result.ok(testtbs);
    }
}
