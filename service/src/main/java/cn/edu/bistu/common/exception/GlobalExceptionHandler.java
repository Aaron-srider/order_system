package cn.edu.bistu.common.exception;

import cn.edu.bistu.workOrder.exception.AttachmentNotExistsException;
import cn.edu.bistu.constants.ResultCodeEnum;
import cn.edu.bistu.model.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.omg.SendingContext.RunTime;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常处理类
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 统一处理返回值
     * @return
     */
    @ExceptionHandler({ResultCodeException.class})
    @ResponseBody
    public Result frontDataMissingException(ResultCodeException ex){
        if(!(ex.getExceptionInfo() == null)) {
            log.debug(ex.getCode().toString() + ":");
        }

        log.debug(ex.getExceptionInfo().toString());
        return Result.build(ex.getExceptionInfo(), ex.getCode());
    }

    /**
     * 统一处理其他后端异常
     * @return
     */
    @ExceptionHandler({RuntimeException.class})
    @ResponseBody
    public Result runtimeException(RuntimeException ex){
        log.error("exception:", ex);
        return Result.build(ex.getClass().getTypeName(), ResultCodeEnum.BACKEND_ERROR);
    }


}
