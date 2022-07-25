package cn.edu.bistu.externalApi.extra.exception;

/**
 * 用于标明为Gpu管理系统相关异常类
 */
public class GpuSysErrorException extends RuntimeException {
    public GpuSysErrorException(String s) {
        super(s);
    }
}
