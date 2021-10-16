package cn.edu.bistu.workOrder.mapper;

import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.model.vo.WorkOrderVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface WorkOrderMapper extends BaseMapper<WorkOrder>{

    /**
     * 工单真删除
     * @param workOrderId 待删除的工单的工单id
     */
    public void deleteWorkOrderByWorkOrderId(Long workOrderId);


    /**
     * 获取分页的工单数据
     * @param workOrderVo 过滤条件
     */
    public List<WorkOrderVo> getWorkOrderPageByConditions(@Param("skip") long skip,@Param("size") long size,@Param("workOrderVo") WorkOrderVo workOrderVo);


    /**
     * 获取分页的工单数据
     * @param workOrderVo 过滤条件
     */
    public long getWorkOrderCountByConditions(@Param("workOrderVo") WorkOrderVo workOrderVo);


    /**
     * 获取单个工单的详细信息
     * @param id 工单id
     */
    public WorkOrderVo getOneWorkOrderById(long id);

    ///**
    // * 获取指定审批者待审批的工单
    // * @param approverId 审批者id
    // * @param workOrderVo 筛选条件
    // */
    //public List<WorkOrderVo> getApprovalWorkOrderPageByApproverId(@Param("skip") long skip,@Param("size") long size,@Param("approverId") long approverId, @Param("workOrderVo") WorkOrderVo workOrderVo);
    //
    ///**
    // * 获取指定审批者待审批的工单的总条数
    // * @param approverId 审批者id
    // * @param workOrderVo 筛选条件
    // */
    //public long getApprovalWorkOrderCountByApproverId(@Param("approverId") long approverId, @Param("workOrderVo") WorkOrderVo workOrderVo);


}
