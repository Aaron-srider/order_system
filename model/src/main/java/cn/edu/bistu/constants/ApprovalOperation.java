package cn.edu.bistu.constants;

public enum  ApprovalOperation {

    PASS("审批通过", 0),
    REJECT("审批不通过", 1),
    ;

    private String operationName;

    private Integer code;

    ApprovalOperation(String operationName, Integer code) {
        this.operationName = operationName;
        this.code = code;
    }

    public String getOperationName() {
        return operationName;
    }

    public Integer getCode() {
        return code;
    }

}
