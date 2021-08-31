package cn.edu.bistu.common.exception;

import cn.edu.bistu.constants.ResultCodeEnum;
import lombok.Data;

@Data
public class AttachmentNotExistsException extends ResultCodeException {

    public AttachmentNotExistsException() {
    }

    public AttachmentNotExistsException(Object exceptionInfo, ResultCodeEnum code) {
        super(exceptionInfo, code);
    }
}
