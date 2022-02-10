package cn.edu.bistu.wx.rest;

import cn.edu.bistu.utils.auth.service.AuthService;
import cn.edu.bistu.model.wx.JsonMiniTemplateMessageNotification;
import cn.edu.bistu.wx.service.WxMiniApi;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Slf4j
@CrossOrigin
@RestController
public class WxController {

    @Autowired
    WxMiniApi wxMiniApi;


    @Autowired
    AuthService authService;


    /**
     * 用于开发者服务器接入微信小程序服务器，微信服务器发送接入信息到此接口，
     * 接口验证信息并返回信息给微信服务器
     * @return 如果验证成功，返回随机数echostr；否则返回"fail"
     */
    @GetMapping("/wx")
    public String access2server(HttpServletRequest req) {
        String sig = req.getParameter("signature");
        String times = req.getParameter("timestamp");
        String nonce = req.getParameter("nonce");
        String echostr = req.getParameter("echostr");
        log.debug("sig :" + sig);
        log.debug("times :" + times);
        log.debug("nonce :" + nonce);
        log.debug("echostr :" + echostr);

        if (wxMiniApi.check(sig, times, nonce)) {
            log.debug("successfully access to wx developer platform");
            return echostr;
        }
        log.debug("access to wx developer platform failed");
        return "fail";
    }

    /**
     * 接收微信服务器的消息推送，告知服务器消息推送是否成功，数据格式是json
     * @param json 推送消息的json
     */
    @PostMapping(value = "/wx", consumes = "application/json", produces = "application/json")
    public Object acceptWxServerMsg(@RequestBody Map<String, Object> json)  {
        JSONObject jsonObject = new JSONObject(json);

        JsonMiniTemplateMessageNotification msgMap = jsonObject.toJavaObject(JsonMiniTemplateMessageNotification.class);

        log.debug(msgMap.toString());
        return null;
    }

    @PostMapping(value = "/manuallyRegister", consumes = "application/json", produces = "application/json")
    public void register(@RequestBody String json) {

        JSONObject jsonObject = JSONObject.parseObject(json);

        String code = (String) jsonObject.get("code");

        authService.authentication(code);
    }
}
