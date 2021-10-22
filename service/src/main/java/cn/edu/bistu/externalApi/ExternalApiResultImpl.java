package cn.edu.bistu.externalApi;

import cn.edu.bistu.constants.ApprovalOperation;
import lombok.Data;

@Data
public class ExternalApiResultImpl implements ExternalApiResult {

    ApprovalOperation approvalOperation;

    @Override
    public ApprovalOperation getWorkOrderStatusOfExcution() {
        return approvalOperation;
    }
}
