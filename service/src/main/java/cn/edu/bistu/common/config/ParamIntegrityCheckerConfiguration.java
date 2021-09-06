package cn.edu.bistu.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;


@Configuration
public class ParamIntegrityCheckerConfiguration {
    @Bean
    @Scope("prototype")
    //@ConditionalOnBean(Validator.class)
    public ParamIntegrityChecker paramIntegrityChecker() {
        ParamIntegrityChecker paramIntegrityChecker = new ParamIntegrityChecker();
        return paramIntegrityChecker;
    }
}
