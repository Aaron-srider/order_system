package cn.edu.bistu.model.common.result;

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
