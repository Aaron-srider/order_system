package cn.edu.bistu.workOrder.service;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
public class FlowNodeApproverDeciderFactory implements ApplicationContextAware, InitializingBean {
    private static Map<String, FlowNodeApproverDecider> FLOW_NODE_APPROVER_DECIDER = new HashMap<>();

    private ApplicationContext applicationContext;

    public FlowNodeApproverDecider getApproverDecider(Long approverId) {
        if (approverId < 0) {
            return FLOW_NODE_APPROVER_DECIDER.get("logicType");
        } else if (approverId > 0) {
            return FLOW_NODE_APPROVER_DECIDER.get("concreteType");
        }
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        applicationContext.getBeansOfType(FlowNodeApproverDecider.class).values().forEach((oneFlowNodeApproverDeciderFromSpringFactory) -> {
            FLOW_NODE_APPROVER_DECIDER.put(oneFlowNodeApproverDeciderFromSpringFactory.getType(), oneFlowNodeApproverDeciderFromSpringFactory);
        });
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
