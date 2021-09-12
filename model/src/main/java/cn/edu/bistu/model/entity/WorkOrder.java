package cn.edu.bistu.model.entity;

import cn.edu.bistu.model.common.validation.Insert;
import cn.edu.bistu.model.common.validation.Query;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.Date;



@Data
public class WorkOrder extends BaseEntity{

    /**
     * 1表示已经审批过，0表示从未被审批
     */
    @TableField(fill= FieldFill.INSERT)
    @Null(groups = {Insert.class})
    private Integer isExamined;


    private Long initiatorId;

    @NotNull(groups = {Insert.class})
    private Long flowId;

    @NotNull(groups = {Insert.class})
    private String title;

    @Null(groups = {Insert.class})
    private byte[] attachment;

    @Null(groups = {Insert.class})
    private String attachmentName;


    @TableField(fill= FieldFill.INSERT)
    @Null(groups = {Insert.class})
    private Integer status;


    @NotNull(groups = {Insert.class})
    private String content;

    @Null(groups = {Insert.class})
    Long flowNodeId;

    @Null(groups = {Insert.class})
    Integer isFinished;

    /**
     * 判断工单是否已经完结
     * @return 若isFinished字段为1，返回true；否则返回false。
     */
    public boolean workOrderFinished() {
        return isFinished.equals(1);
    }


}
