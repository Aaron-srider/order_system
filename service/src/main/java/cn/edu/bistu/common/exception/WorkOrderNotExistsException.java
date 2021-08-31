package cn.edu.bistu.common.exception;

import cn.edu.bistu.constants.ResultCodeEnum;
import lombok.Data;

@Data
public class WorkOrderNotExistsException extends ResultCodeException{

    public WorkOrderNotExistsException() {
    }

    public WorkOrderNotExistsException(Object exceptionInfo, ResultCodeEnum code) {
        super(exceptionInfo, code);
    }
}
