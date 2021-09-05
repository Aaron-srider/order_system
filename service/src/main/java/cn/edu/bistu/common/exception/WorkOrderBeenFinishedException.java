package cn.edu.bistu.common.exception;

import cn.edu.bistu.constants.ResultCodeEnum;

public class WorkOrderBeenFinishedException extends ResultCodeException {
    public WorkOrderBeenFinishedException() {
    }

    public WorkOrderBeenFinishedException(Object exceptionInfo, ResultCodeEnum code) {
        super(exceptionInfo, code);
    }
}
