package cn.edu.bistu.common.config;

import cn.edu.bistu.common.interceptor.AuthorizationInterceptor;
import cn.edu.bistu.common.interceptor.CORSInterceptor;
import cn.edu.bistu.common.interceptor.LogInterceptor;
import cn.edu.bistu.common.interceptor.AuthenticationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;


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


        //跨域拦截优先
        HandlerInterceptor corsInterceptor = getMyInterceptors(CORSInterceptor.class);
        registry.addInterceptor(corsInterceptor)
        ;

        enable(registry, true);


    }

    public void enable(InterceptorRegistry registry, boolean enable) {
        if(enable) {
            /**
             * 打印来访日志
             */
            HandlerInterceptor logInterceptor = getMyInterceptors(LogInterceptor.class);
            registry.addInterceptor(logInterceptor)
            ;

            //用户认证
            HandlerInterceptor authenticationInterceptor = getMyInterceptors(AuthenticationInterceptor.class);
            registry.addInterceptor(authenticationInterceptor)
                    //用户登录不拦截
                    .excludePathPatterns("/auth/login")
                    .excludePathPatterns("/admin/login")
                    .excludePathPatterns("/auth/userInfoCompletion/**")
                    .excludePathPatterns("/wx")
                    .excludePathPatterns("/test/**")
                    //暂时登录测试接口不拦截
                    .excludePathPatterns("/utils/**")
                    //关于流程的查询不需要权限和登录
                    .excludePathPatterns("/flows/**")

                    // Gpu 管理系统访问不拦截
                    .excludePathPatterns("/gpu/api/message")
            ;

            //用户授权
            HandlerInterceptor authorizationInterceptor = getMyInterceptors(AuthorizationInterceptor.class);
            registry.addInterceptor(authorizationInterceptor)
                    //用户登录不拦截
                    .excludePathPatterns("/auth/login")
                    .excludePathPatterns("/auth/userInfoCompletion/**")
                    .excludePathPatterns("/admin/login")
                    .excludePathPatterns("/wx")
                    .excludePathPatterns("/test/**")
                    .excludePathPatterns("/message/**")
                    //暂时登录测试接口不拦截
                    .excludePathPatterns("/utils/**")
                    //关于流程的查询不需要权限和登录
                    .excludePathPatterns("/flows/**")

                    // Gpu 管理系统访问不拦截
                    .excludePathPatterns("/gpu/api/message")

             ;
        }
    }




}
