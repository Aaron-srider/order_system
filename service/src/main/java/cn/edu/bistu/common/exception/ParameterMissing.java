package cn.edu.bistu.common.exception;

import lombok.Data;

import java.util.List;

@Data
public class ParameterMissing extends RuntimeException {

    List<String> missingParams ;

    public ParameterMissing(List<String> missingParams) {
        this.missingParams = missingParams;
    }
}
