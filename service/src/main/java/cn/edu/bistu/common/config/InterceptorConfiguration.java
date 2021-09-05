package cn.edu.bistu.common.config;

import cn.edu.bistu.common.interceptor.AuthorizationInterceptor;
import cn.edu.bistu.common.interceptor.LogInterceptor;
import cn.edu.bistu.common.interceptor.AuthenticationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class InterceptorConfiguration implements WebMvcConfigurer {
    @Autowired
    ApplicationContext applicationContext;

    public <T> T getMyInterceptors(Class<T> clazz) {
        T bean = applicationContext.getBean(clazz);
        return bean;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        /**
         * 打印来访日志
         */
        HandlerInterceptor logInterceptor = getMyInterceptors(LogInterceptor.class);
        registry.addInterceptor(logInterceptor)
        ;

        //用户认证
        HandlerInterceptor authenticationInterceptor = getMyInterceptors(AuthenticationInterceptor.class);
        registry.addInterceptor(authenticationInterceptor)
                .excludePathPatterns("/auth/login")
                .excludePathPatterns("/auth/userInfoCompletion")
                .excludePathPatterns("/wx")
        ;

        //用户授权
        HandlerInterceptor authorizationInterceptor = getMyInterceptors(AuthorizationInterceptor.class);
        registry.addInterceptor(authorizationInterceptor)
                .excludePathPatterns("/auth/login")
                .excludePathPatterns("/auth/userInfoCompletion")
                .excludePathPatterns("/wx")
        ;

    }


}
