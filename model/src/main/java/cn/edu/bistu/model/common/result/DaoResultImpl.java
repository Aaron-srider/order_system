package cn.edu.bistu.model.common.result;

import cn.edu.bistu.model.entity.College;
import cn.edu.bistu.model.entity.Major;
import cn.edu.bistu.model.entity.auth.Role;
import cn.edu.bistu.model.entity.auth.User;
import com.alibaba.fastjson.JSONObject;

import java.util.List;


public class DaoResultImpl<T> implements DaoResult<T>{

    /**
     * 内部的JSONObject
     */
    private JSONObject value = new JSONObject();

    @Override
    public void setValue(JSONObject value) {
        this.value = value;
    }

    public JSONObject getValue() {
        return value;
    }

    public T getResult() {
        return (T) value.get("result");
    }

    public DaoResult<T> setResult(T result) {
        //this.result = result;
        value.put("result", result);
        return this;
    }

    public JSONObject getDetailInfo() {
        return (JSONObject) value.get("detailInfo");
    }

    public DaoResult<T> setDetailInfo(JSONObject detailInfo) {
        //this.detailInfo = detailInfo;
        value.put("detailInfo", detailInfo);
        return this;
    }

    public void addDetailInfo(String key, Object obj) {
        if (getDetailInfo() == null) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(key, obj);
            setDetailInfo(jsonObject);
        } else {
            JSONObject detailInfo = getDetailInfo();
            detailInfo.put(key, obj);
        }
    }

    @Override
    public String toString() {
        return value.toJSONString();
    }



}


