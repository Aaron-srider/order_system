package cn.edu.bistu.externalApi;

import cn.edu.bistu.approval.WorkOrderFinisher;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 该类作用与 WorkOrderFinisherFactory 工厂类的作用相似，只不过该工厂返回接口 ExternalApi 的实现类。
 * 注意如果试图向工厂中添加实现类，需要添加实现类的Class文件到项目中（或者添加实现类的源码，并重新编译整个项目），
 * 并重启整个SpringBoot项目，Spring将自动扫描Spring容器中所有的 ExternalApi 的实现类，并通过接口 ExternalApi
 * 获取实现类的标识，并将实现类组织到Map中，运行过程中直接使用getImplementation工厂方法获取对应实现类。
 *
 * 注意，要注册外部实现类到本系统中，需要在数据库表的approver_logic表中插入对应的记录，包括实现类的标识（对应表的
 * text字段），ype字段设置为 system ，value字段自定义一个Int值（一般接着表中上一个记录顺序后延），同时还要将实现类的Class
 * 文件放到ClassPath中，以便Spring能扫描到。
 * @author wc
 */
@Component
public class ExternalApiImplementationFactory implements InitializingBean, ApplicationContextAware {

    /**
     * 存放不同类型的 ExternalApi 的实现类
     */
    private static final Map<String, ExternalApi> EXTERNAL_API_IMPLEMENTATION_MAP = new HashMap<>();

    private ApplicationContext applicationContext;

    /**
     * 根据ExternalApi 实现类的标识获取对应的 ExternalApi 的实现类
     * @param implementationSignature ExternalApi 实现类的标识，根据此标识去Map中查找实现类
     * @return 指定 implementationSignature 的 ExternalApi 实现类
     */
    public ExternalApi getImplementation(String implementationSignature) {
        return EXTERNAL_API_IMPLEMENTATION_MAP.get(implementationSignature);
    }

    public void afterPropertiesSet()  {
        applicationContext.getBeansOfType(ExternalApi.class).values()
                .forEach(externalApi -> EXTERNAL_API_IMPLEMENTATION_MAP.put(externalApi.getSignature(), externalApi));
        System.out.println(EXTERNAL_API_IMPLEMENTATION_MAP);
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}