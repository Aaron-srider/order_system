package cn.edu.bistu.common.exception;


import cn.edu.bistu.constants.ResultCodeEnum;

public class UserNotRegisteredException extends ResultCodeException{
    public UserNotRegisteredException(Object exceptionInfo, ResultCodeEnum code) {
        super(exceptionInfo, code);
    }
}
