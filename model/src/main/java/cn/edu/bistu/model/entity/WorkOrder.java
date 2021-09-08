package cn.edu.bistu.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;


@Data
public class WorkOrder extends BaseEntity{

    /**
     * 1表示已经审批过，0表示从未被审批
     */
    @TableField(fill= FieldFill.INSERT)
    private Integer isExamined;

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

    Long flowNodeId;

    Integer isFinished;

    /**
     * 判断工单是否已经完结
     * @return 若isFinished字段为1，返回true；否则返回false。
     */
    public boolean workOrderFinished() {
        return isFinished.equals(1);
    }


}
