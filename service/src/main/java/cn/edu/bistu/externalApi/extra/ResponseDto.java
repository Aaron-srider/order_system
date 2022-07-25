package cn.edu.bistu.externalApi.extra;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ResponseDto {
    Integer code;
    String msg;
}
