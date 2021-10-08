package cn.edu.bistu.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;


@Data
public class WorkOrderHistory extends BaseEntity{

    private Long workOrderId;

    /**
     * 记录工单结束前的位置
     */
    private Long flowNodeId;

    @NotNull
    private Long initiatorId;
    @NotNull
    private Long flowId;

    @NotNull
    private String title;

    private byte[] attachment;
    private String attachmentName;

    @TableField(fill= FieldFill.INSERT)
    private Integer status;

    @NotNull
    private String content;


    private Integer beforeFinishedStatus;


}
