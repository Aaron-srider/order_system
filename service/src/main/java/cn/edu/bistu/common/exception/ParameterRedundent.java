package cn.edu.bistu.common.exception;

import lombok.Data;

import java.util.List;


@Data
public class ParameterRedundent extends RuntimeException {

    List<String> redundentParams ;

    public ParameterRedundent(List<String> redundentParams) {
        this.redundentParams = redundentParams;
    }
}
