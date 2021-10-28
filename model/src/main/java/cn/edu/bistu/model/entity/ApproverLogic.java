package cn.edu.bistu.model.entity;

import lombok.Data;

@Data
public class ApproverLogic implements FlowNodeApprover{
    private Long id;
    private String text;
    private Integer value;
    private String type;
    private String name;
}
