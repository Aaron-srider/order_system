package cn.edu.bistu.common.exception;

import cn.edu.bistu.constants.ResultCodeEnum;

public class UserInfoNotCompleteException extends ResultCodeException {
    public UserInfoNotCompleteException() {
    }

    public UserInfoNotCompleteException(Object exceptionInfo, ResultCodeEnum code) {
        super(exceptionInfo, code);
    }

}
