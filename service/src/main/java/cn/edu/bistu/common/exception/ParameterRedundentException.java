package cn.edu.bistu.common.exception;

import lombok.Data;

import java.util.List;


@Data
public class ParameterRedundentException extends RuntimeException {

    List<String> redundentParams ;

    public ParameterRedundentException(List<String> redundentParams) {
        this.redundentParams = redundentParams;
    }
}
