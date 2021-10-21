package cn.edu.bistu.model.vo;

import cn.edu.bistu.model.entity.Flow;
import cn.edu.bistu.model.entity.FlowNode;
import cn.edu.bistu.model.entity.WorkOrder;
import lombok.Data;



@Data
public class WorkOrderVo extends WorkOrder{

    /**
     * 筛选条件：工单创建时间
     */
    String startDate;

    /**
     * 筛选条件：工单创建时间
     */
    String endDate;

    /**
     * 筛选条件：发起人学号/工号
     */
    String studentJobId;

    /**
     * 返回给前端的附加数据，在一次查询中全部装配
     */
    Flow flow;

    FlowNode flowNode;

    UserVo initiator;
}
