package cn.edu.bistu.approval.mapper;

import cn.edu.bistu.model.entity.ApprovalRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface ApprovalRecordMapper extends BaseMapper<ApprovalRecord>{

    /**
     * 工单审批记录（真删除）
     * @param workOrderId 根据该工单id将相应的审批记录真删除
     */
    public void deleteWorkOrderApprovalRecordsByWorkOrderId(Long workOrderId);

}
