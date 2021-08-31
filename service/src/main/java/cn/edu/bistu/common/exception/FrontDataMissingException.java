package cn.edu.bistu.common.exception;

import cn.edu.bistu.constants.ResultCodeEnum;
import lombok.Data;


@Data
public class FrontDataMissingException extends ResultCodeException {

    public FrontDataMissingException(Object exceptionInfo, ResultCodeEnum code) {
        super(exceptionInfo, code);
    }
}
