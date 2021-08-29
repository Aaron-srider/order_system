package cn.edu.bistu.common.exception;

import lombok.Data;

import java.util.List;

@Data
public class ParameterMissingException extends RuntimeException {

    List<String> missingParams ;

    public ParameterMissingException(List<String> missingParams) {
        this.missingParams = missingParams;
    }
}
