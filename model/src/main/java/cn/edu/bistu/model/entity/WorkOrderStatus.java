package cn.edu.bistu.model.entity;

import lombok.Data;

@Data
public class WorkOrderStatus {
    Long id;
    Integer value;
    String text;
    String alias;
}
