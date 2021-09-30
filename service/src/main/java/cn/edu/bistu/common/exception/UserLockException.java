package cn.edu.bistu.common.exception;

import cn.edu.bistu.constants.ResultCodeEnum;

public class UserLockException extends ResultCodeException{

    public UserLockException() {
    }

    public UserLockException(Object exceptionInfo, ResultCodeEnum code) {
        super(exceptionInfo, code);
    }

}
