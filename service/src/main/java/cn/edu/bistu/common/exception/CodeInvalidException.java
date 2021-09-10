package cn.edu.bistu.common.exception;

import cn.edu.bistu.constants.ResultCodeEnum;

public class CodeInvalidException extends ResultCodeException {
    public CodeInvalidException(Object exceptionInfo, ResultCodeEnum code) {
        super(exceptionInfo, code);
    }
}
