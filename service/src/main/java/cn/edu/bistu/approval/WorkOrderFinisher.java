package cn.edu.bistu.approval;


/**
 * 工单结束者，在每次工单审批后将工单结束，WorkOrderFinisher 的实现类可能需要从 Spring 容器中获取组件，
 * 所以将其所有实现类都创建并存放在静态的 Map 中，以工厂的形式得到具体实现类，所以如果需要使用 WorkOrderFinisher
 * 类，需要在使用上下文中注入 WorkOrderFinisherFactory ，然后调用其getFinisher方法以获取实现类，具体请
 * 查看 WorkOrderFinisherFactory
 * 工厂类。
 * @author wc
 */
public interface WorkOrderFinisher {

    /**
     * 返回工单结束者的类型
     * @return 返回工单结束者的类型
     */
    public String getType();

    /**
     * 结束工单的具体算法
     * @param workOrderFinishWrap 包含了结束工单所具备的参数，详见 WorkOrderFinishWrap 类
     */
    public void finishWorkOrder(WorkOrderFinishWrapper workOrderFinishWrap);
}
