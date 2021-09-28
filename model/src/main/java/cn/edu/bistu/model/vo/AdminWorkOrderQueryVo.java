package cn.edu.bistu.model.vo;

import lombok.Data;

import java.util.Date;

@Data
public class AdminWorkOrderQueryVo {
    Long id;

    String studentJobId;

    String startDate;

    String endDate;
}
