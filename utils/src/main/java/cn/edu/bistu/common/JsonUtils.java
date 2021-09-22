package cn.edu.bistu.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class JsonUtils {

    public static JSONObject convertObj2JsonObj(Object obj) {
        String s = JSON.toJSONString(obj);
        JSONObject jsonObject = JSONObject.parseObject(s);
        return jsonObject;
    }
}
