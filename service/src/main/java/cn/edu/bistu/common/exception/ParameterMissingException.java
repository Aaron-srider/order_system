package cn.edu.bistu.common.exception;

import cn.edu.bistu.constants.ResultCodeEnum;
import lombok.Data;

import java.util.List;

@Data
public class ParameterMissingException extends ResultCodeException {
    public ParameterMissingException(Object exceptionInfo, ResultCodeEnum code) {
        super(exceptionInfo, code);
    }
}
