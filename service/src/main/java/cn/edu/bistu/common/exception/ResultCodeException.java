package cn.edu.bistu.common.exception;

import cn.edu.bistu.constants.ResultCodeEnum;
import lombok.Data;

import javax.validation.constraints.NotNull;


/**
 * 错误返回码对应的异常的父类
 */
@Data
public class ResultCodeException extends RuntimeException{

    /**
     * 错误返回码，必须传递
     */
    @NotNull
    private ResultCodeEnum code;

    /**
     * 错误信息，可以不用传递
     */
    private Object exceptionInfo;

    public ResultCodeException() {
    }

    public ResultCodeException(Object exceptionInfo, ResultCodeEnum code) {
        this.exceptionInfo = exceptionInfo;
        if(code == null) {
            throw new IllegalArgumentException();
        }
        this.code = code;
    }
}
