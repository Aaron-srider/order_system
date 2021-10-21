package cn.edu.bistu.admin.workOrder.exception;

import cn.edu.bistu.common.exception.ResultCodeException;
import cn.edu.bistu.constants.ResultCodeEnum;

public class AttachmentNotExistsException extends ResultCodeException {

    public AttachmentNotExistsException() {
    }

    public AttachmentNotExistsException(Object exceptionInfo, ResultCodeEnum code) {
        super(exceptionInfo, code);
    }
}
