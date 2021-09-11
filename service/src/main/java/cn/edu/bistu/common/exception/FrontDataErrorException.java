package cn.edu.bistu.common.exception;

import cn.edu.bistu.constants.ResultCodeEnum;

public class FrontDataErrorException extends ResultCodeException {
    public FrontDataErrorException(Object exceptionInfo, ResultCodeEnum code) {
        super(exceptionInfo, code);
    }
}
