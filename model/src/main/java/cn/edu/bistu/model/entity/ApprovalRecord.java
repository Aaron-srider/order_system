package cn.edu.bistu.model.entity;


import cn.edu.bistu.model.common.validation.Insert;
import cn.edu.bistu.model.common.validation.Update;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 工单与审批节点之间的关系实体，用于记录工单在流程中的节点位置
 */
@Data
public class ApprovalRecord extends BaseEntity
{

    /**
     * 审批操作，0通过，1不通过
     */
    private Integer operation;

    /**
     * 审批操作（无论是通过还是不通过）的时间戳
     */
    private Date approvalDatetime;

    /**
     * 审批留言
     */
    private String comment;


    /**
     * 审批对应的工单id
     */
    @NotNull(groups = {Update.class})
    private Long workOrderId;

    /**
     * 审批对应的节点id
     */
    private Long flowNodeId;


    /**
     * 审批对应的审批人id
     */
    private Long approverId;
}
