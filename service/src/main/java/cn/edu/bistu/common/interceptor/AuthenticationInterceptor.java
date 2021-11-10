package cn.edu.bistu.common.interceptor;

import cn.edu.bistu.user.dao.UserDao;
import cn.edu.bistu.auth.JwtHelper;
import cn.edu.bistu.common.MapService;
import cn.edu.bistu.common.ResponseHelper;
import cn.edu.bistu.constants.ResultCodeEnum;
import cn.edu.bistu.model.common.result.DaoResult;
import cn.edu.bistu.model.common.result.Result;
import cn.edu.bistu.model.entity.auth.User;
import cn.edu.bistu.model.vo.UserVo;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;

@Component
@Slf4j
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Autowired
    UserDao userDao;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if("get".equals(request.getMethod().toLowerCase()) && request.getServletPath().contains("/workOrder/attachment/")) {
            return true;
        }

        log.debug("get into AuthenticationInterceptor");



        //获取token
        String token = request.getHeader("token");

        if (token == null) {
            //签名丢失
            ResponseHelper.returnJson(response, Result.build(null, ResultCodeEnum.TOKEN_MISSING));
            log.error(ResultCodeEnum.TOKEN_MISSING.toString());
            return false;
        }

        //验证token
        try {
            Jws<Claims> claimsJws = JwtHelper.verifySignature(token);
        } catch (SignatureException ex) {
            //签名认证错误
            ResponseHelper.returnJson(response, Result.build(null, ResultCodeEnum.SIGN_ERROR));
            log.error(ResultCodeEnum.SIGN_ERROR.toString());
            return false;
        } catch (ExpiredJwtException ex) {
            //token失效
            ResponseHelper.returnJson(response, Result.build(null, ResultCodeEnum.TOKEN_EXPIRED));
            log.error(ResultCodeEnum.TOKEN_EXPIRED.toString());
            return false;
        } catch (UnsupportedJwtException ex) {
            //签名丢失
            ResponseHelper.returnJson(response, Result.build(null, ResultCodeEnum.SIGN_MISSING));
            log.error(ResultCodeEnum.SIGN_MISSING.toString());
            return false;
        } catch (MalformedJwtException ex) {
            //token格式错误（要么无法读取token头中的json数据，要么token的'.'分割符少于两个）
            ResponseHelper.returnJson(response, Result.build(null, ResultCodeEnum.TOKEN_FORMAT_ERROR));
            log.error(ResultCodeEnum.TOKEN_FORMAT_ERROR.toString());
            return false;
        } catch (CompressionException ex) {
            //token体解压错误
            ResponseHelper.returnJson(response, Result.build(null, ResultCodeEnum.TOKEN_BODY_ERROR));
            log.error(ResultCodeEnum.TOKEN_BODY_ERROR.toString());
            return false;
        } catch (Exception ex) {
            //其他token错误
            ResponseHelper.returnJson(response, Result.build(null, ResultCodeEnum.TOKEN_ERROR));
            log.error(ResultCodeEnum.TOKEN_ERROR.toString());
            return false;
        }


        Long id = JwtHelper.getClaim(token, "id", Integer.class).longValue();


        DaoResult<UserVo> oneUserById = userDao.getOneUserById(id);
        Integer isLock = oneUserById.getResult().getIsLock();
        if (isLock.equals(1)) {
            ResponseHelper.returnJson(response, Result.build(oneUserById.getValue(), ResultCodeEnum.USER_LOCK));

            log.error(ResultCodeEnum.USER_LOCK.toString());
            return false;
        }

        log.info("用户id:" + id);

        MapService mapService = MapService.map()
                .putMap("id", id);
        request.setAttribute("userInfo", mapService);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {

    }

    public static void main(String[] args) {
        User user = new User();

        //user.setCreateTime(new Date());
        SimpleDateFormat dateFormat = new SimpleDateFormat();
        dateFormat.applyPattern("yyyy-MM-dd HH:mm:ss");
        String format = dateFormat.format(user.getCreateTime());
        System.out.println(format);
        System.out.println(user.getCreateTime());

    }
}
