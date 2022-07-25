package cn.edu.bistu.externalApi.extra.exception;

/**
 * <p>将工单系统用户表单转换成Gpu管理系统所需格式时出现的异常，仅会在以下情况抛出：
 * <p>1.工单系统中申请表单不正确填写
 * <p>2.工单系统中用户工/学号无法转换成长整型
 * <p>
 *     @author YHK
 */
public class GpuSysUserFormatException extends GpuSysErrorException{
    public GpuSysUserFormatException(String message) {
        super(message);
    }
}
