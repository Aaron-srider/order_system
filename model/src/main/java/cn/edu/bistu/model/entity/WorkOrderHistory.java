package cn.edu.bistu.model.entity;

import lombok.Data;

@Data
public class WorkOrderHistory extends BaseEntity{

    /**
     * 指向工单本身
     */
    private Long workOrderId;

    /**
     * 记录工单结束前的位置
     */
    private Long beforeFinishedFlowNodeId;

    /**
     * 记录工单结束状态的位置
     */
    private Integer beforeFinishedStatus;


}
