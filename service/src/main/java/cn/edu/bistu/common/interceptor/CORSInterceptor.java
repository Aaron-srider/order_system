package cn.edu.bistu.common.interceptor;

import cn.edu.bistu.auth.JwtHelper;
import cn.edu.bistu.common.MapService;
import cn.edu.bistu.common.ResponseHelper;
import cn.edu.bistu.common.exception.HttpOptionsMethodException;
import cn.edu.bistu.constants.ResultCodeEnum;
import cn.edu.bistu.model.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class CORSInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        log.debug("===============Start process a request===============");

        log.debug("get into CORSInterceptor");

        // 如果是OPTIONS则结束请求
        if (HttpMethod.OPTIONS.toString().equals(request.getMethod())) {
            log.debug(request.getMethod() + " " + request.getServletPath() + " was blocked because it is an options request");
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Methods", "GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS");
            response.setHeader("Access-Control-Max-Age", "86400");
            response.setHeader("Access-Control-Allow-Headers", "*");
            return false;
            //throw new HttpOptionsMethodException("");
        }

        return true;

    }

}
