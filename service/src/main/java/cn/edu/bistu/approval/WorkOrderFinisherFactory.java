package cn.edu.bistu.approval;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于获取 WorkOrderFinisher 不同的具体实现类，在 Spring 创建该工厂对象时，从 Spring 容器中取出实现类
 * ，并按照 WorkOrderFinisher 的 getType() 方法将实现类组织到 Map 中，通过工厂方法 getFinisher() 返回
 * 具体的实现类。
 * @author wc
 */
@Component
public class WorkOrderFinisherFactory implements InitializingBean, ApplicationContextAware {

    /**
     * 存放不同类型的finisher
     */
    private static final Map<String, WorkOrderFinisher> WORK_ORDER_FINISHER_MAP = new HashMap<>();

    private ApplicationContext applicationContext;

    /**
     * 根据结束类型获取对应的处理器
     * @param finisherType 结束类型
     * @return 提交类型对应的处理器
     */
    public WorkOrderFinisher getFinisher(String finisherType) {
        return WORK_ORDER_FINISHER_MAP.get(finisherType);
    }

    public void afterPropertiesSet()  {
        // 将 Spring 容器中所有的 Finisher 注册到 WORK_ORDER_FINISHER_MAP
        applicationContext.getBeansOfType(WorkOrderFinisher.class).values()
                .forEach(finisher -> WORK_ORDER_FINISHER_MAP.put(finisher.getType(), finisher));
        System.out.println(WORK_ORDER_FINISHER_MAP);
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}