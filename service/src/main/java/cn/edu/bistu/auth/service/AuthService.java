package cn.edu.bistu.auth.service;

import cn.edu.bistu.model.common.Result;

public interface AuthService{
    /**
     * 用户认证，为登录接口提供服务。
     * 检查用户是否注册，若未注册，为用户自动注册；若已注册，认证用户身份
     * @param code 微信临时登录凭证
     * @return 返回认证结果，若认证通过，返回用户信息和登录token（包含用户id）
     */
    Result authentication(String code);


    /**
     * 用户授权，为授权拦截器提供服务。
     * 检查用户的权限是否足以访问当前api
     * @param id 用户id
     * @param requestURL api的url
     * @param requestMethod api的请求方式
     * @return 如果权限足够，返回true，如果权限不足，返回false
     */
    boolean authorization(Long id, String requestURL, String requestMethod);
}