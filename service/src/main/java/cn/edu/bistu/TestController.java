package cn.edu.bistu;

import cn.edu.bistu.model.vo.UserVo;
import org.junit.Test;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.BiFunction;


@RestController
@Validated
@CrossOrigin
public class TestController {
    @PostMapping("/test")
    public Entity testPost(@RequestBody UserVo userVo, HttpServletRequest req) {
        System.out.println(userVo);

        String token = req.getHeader("token");
        System.out.println(token);
        Entity entity = new Entity();
        entity.setAge("12");
        entity.setId("1");
        entity.setName("wec");
        return entity;
    }

    @GetMapping("/test")
    public Entity testGet(UserVo userVo, HttpServletRequest req) {
        System.out.println(userVo);

        String token = req.getHeader("token");
        System.out.println(token);

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
}
