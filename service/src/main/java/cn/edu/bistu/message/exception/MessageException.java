package cn.edu.bistu.message.exception;

import cn.edu.bistu.common.exception.ResultCodeException;
import cn.edu.bistu.constants.ResultCodeEnum;

/**
 * @author: Mr.Yu
 * @create: 2021-09-13 22:53
 **/
public class MessageException extends ResultCodeException {

    public MessageException(){}

    public MessageException(Object exceptionInfo, ResultCodeEnum code){
        super(exceptionInfo,code);
    }
}
