package cn.edu.bistu.test.rest;

import cn.edu.bistu.Entity;
import cn.edu.bistu.model.common.result.Result;
import cn.edu.bistu.model.vo.UserVo;
import cn.edu.bistu.test.Testtb;
import cn.edu.bistu.test.mapper.TestdbDao;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
