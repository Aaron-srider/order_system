package cn.edu.bistu.common.exception;

import cn.edu.bistu.constants.ResultCodeEnum;

public class CodeBeenUsedException extends ResultCodeException {
    public CodeBeenUsedException(Object exceptionInfo, ResultCodeEnum code) {
        super(exceptionInfo, code);
    }
}
