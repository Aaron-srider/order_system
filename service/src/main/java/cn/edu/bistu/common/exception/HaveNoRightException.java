package cn.edu.bistu.common.exception;

import cn.edu.bistu.constants.ResultCodeEnum;

public class HaveNoRightException extends ResultCodeException{
    public HaveNoRightException(Object exceptionInfo, ResultCodeEnum code) {
        super(exceptionInfo, code);
    }
}
