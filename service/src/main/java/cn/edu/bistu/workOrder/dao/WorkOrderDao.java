package cn.edu.bistu.workOrder.dao;

import cn.edu.bistu.model.common.result.DaoResult;
import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.model.entity.WorkOrderStatus;
import cn.edu.bistu.model.vo.WorkOrderVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface WorkOrderDao {

    public WorkOrderStatus constantToEntity(cn.edu.bistu.constants.WorkOrderStatus statusConstant);

    /**
     * 根据筛选条件查询工单分页数据
     * @param page 分页信息
     * @param condition
     */
    public DaoResult<Page<WorkOrderVo>> getWorkOrderPageByConditions(Page<WorkOrderVo> page, WorkOrderVo workOrderVo, String condition);



    /**
     * 通过工单id获取工单信息
     */
    public DaoResult<WorkOrderVo> getOneWorkOrderById(Long id);



    /**
     * 从审批节点表，工单表中查出指定审批者的待审批工单信息。
     * 主要委托getWorkOrderPageByWrapper接口获取工单数据。
     *
     * @param page        分页数据，包含以下有效数据:
     *                    size：要获取的页数大小
     *                    current：要获取的页数
     * @param approverId  审批者id
     * @param workOrderVo 包含有效数据:
     *                    title
     * @param condition
     * @return
     */
    public DaoResult<Page<WorkOrderVo>> getApprovalWorkOrderPage(Page<WorkOrderVo> page, Long approverId, WorkOrderVo workOrderVo, String condition);


    public void updateById(WorkOrder workOrder);
}
