package cn.edu.bistu.common.interceptor;

import cn.edu.bistu.auth.JwtHelper;
import cn.edu.bistu.utils.auth.service.AuthService;
import cn.edu.bistu.common.MapService;
import cn.edu.bistu.common.ResponseHelper;
import cn.edu.bistu.constants.ResultCodeEnum;
import cn.edu.bistu.model.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class AuthorizationInterceptor implements HandlerInterceptor {

    @Autowired
    AuthService authService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if("get".equals(request.getMethod().toLowerCase()) && request.getServletPath().contains("/workOrder/attachment/")) {
            return true;
        }

        log.debug("get into AuthorizationInterceptor");



        //从header获取token
        String token=request.getHeader("token");

        //从token中获取id
        Long id = JwtHelper.getClaim(token, "id", Integer.class).longValue();
        log.info("用户id:" + id);

        //获取用户访问的接口路径
        String requestURI = request.getServletPath();
        //获取用户访问的请求方式
        String requestMethod = request.getMethod();

        //用户授权
        boolean isPass = authService.authorization(id, requestURI, requestMethod);

        if(isPass) {
            //授权通过，放行
            MapService mapService = MapService.map()
                    .putMap("id", id);
            request.setAttribute("userInfo", mapService);
            log.debug("授权通过");
            return true;
        } else {
            //授权失败，返回错误代码
            ResponseHelper.returnJson(response, Result.build(null, ResultCodeEnum.AUTHORIZATION_FAIL));
            log.error(ResultCodeEnum.AUTHORIZATION_FAIL.toString());
            return false;
        }
    }

}
