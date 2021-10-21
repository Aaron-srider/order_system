package cn.edu.bistu.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sun.corba.se.impl.ior.OldJIDLObjectKeyTemplate;

import java.util.Set;

public class JsonUtils {

    public static JSONObject convertObj2JsonObj(Object obj) {
        String s = JSON.toJSONString(obj);
        JSONObject jsonObject = JSONObject.parseObject(s);
        return jsonObject;
    }

    public static JSONObject mergeJSONObject(JSONObject... jsonObjectArray) {
        JSONObject result = new JSONObject();
        for (JSONObject jsonObject : jsonObjectArray) {
            Set<String> keySet = jsonObject.keySet();
            for (String key : keySet) {
                Object value=jsonObject.get(key);
                result.put(key, value);
            }
        }
        return result;
    }


}
