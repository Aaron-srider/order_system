package cn.edu.bistu.externalApi;

import cn.edu.bistu.constants.ApprovalOperationEnum;
import lombok.Data;

@Data
public class ExternalApiResultImpl implements ExternalApiResult {

    ApprovalOperationEnum approvalOperation;

    @Override
    public ApprovalOperationEnum getWorkOrderStatusOfExcution() {
        return approvalOperation;
    }
}
