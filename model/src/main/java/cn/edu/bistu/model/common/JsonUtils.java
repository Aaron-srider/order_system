package cn.edu.bistu.model.common;

import cn.edu.bistu.model.common.result.DaoResult;
import cn.edu.bistu.model.common.result.DaoResultImpl;
import com.alibaba.fastjson.JSONObject;

/**
 * 为JSONObject的构造提供一定遍历，方便符合DaoResult的规范
 */
public class JsonUtils {

    public static <T> void setResult(JSONObject jsonObject, T result) {
        DaoResult<T> daoResult = new DaoResultImpl<>();
        daoResult.setValue(jsonObject);
        daoResult.setResult(result);
    }
    public static void setDetailInfo(JSONObject jsonObject, JSONObject detailInfo) {
        DaoResult daoResult = new DaoResultImpl<>();
        daoResult.setValue(jsonObject);
        daoResult.setDetailInfo(detailInfo);
    }


}
