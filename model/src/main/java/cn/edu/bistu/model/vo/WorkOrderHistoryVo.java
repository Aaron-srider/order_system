package cn.edu.bistu.model.vo;


import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.model.entity.WorkOrderHistory;
import lombok.Data;

@Data
public class WorkOrderHistoryVo extends WorkOrderHistory {

    String studentId;
    String jobId;
    String initiatorName;
    String flowName;

    String attachmentUrl;

    Long size;
    Long current;
}

