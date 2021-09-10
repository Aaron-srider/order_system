package cn.edu.bistu.common.exception;

import cn.edu.bistu.constants.ResultCodeEnum;

public class InterfaceAccessException extends  ResultCodeException{
    public InterfaceAccessException() {
    }

    public InterfaceAccessException(Object exceptionInfo, ResultCodeEnum code) {
        super(exceptionInfo, code);
    }
}
