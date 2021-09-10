package cn.edu.bistu.model.common;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public class ServiceResultImpl<T> implements ServiceResult<T> {
    private T data;

    public ServiceResultImpl(T data) {
        this.data = data;
    }

    @Override
    public T getServiceResult() {
        return data;
    }

}
