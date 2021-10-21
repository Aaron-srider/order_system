package cn.edu.bistu.model.common.result;

import com.alibaba.fastjson.JSONObject;

public class SimpleDaoResultImpl<T> implements DaoResult<T> {

    private T data;

    @Override
    public void setValue(JSONObject value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JSONObject getValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public T getResult() {
        return data;
    }

    @Override
    public DaoResult<T> setResult(T result) {
        data=result;
        return this;
    }

    @Override
    public JSONObject getDetailInfo() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DaoResult<T> setDetailInfo(JSONObject detailInfo) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addDetailInfo(String key, Object obj) {
        throw new UnsupportedOperationException();
    }
}
