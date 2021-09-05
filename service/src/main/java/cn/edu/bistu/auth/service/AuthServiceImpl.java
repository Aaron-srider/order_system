package cn.edu.bistu.auth.service;

import cn.edu.bistu.auth.JwtHelper;
import cn.edu.bistu.auth.exception.Jscode2sessionException;
import cn.edu.bistu.auth.mapper.AuthMapper;
import cn.edu.bistu.common.BeanUtils;
import cn.edu.bistu.constants.ResultCodeEnum;
import cn.edu.bistu.model.common.Result;
import cn.edu.bistu.model.WxLoginStatus;
import cn.edu.bistu.model.entity.auth.Permission;
import cn.edu.bistu.model.vo.UserVo;
import cn.edu.bistu.wx.service.WxMiniApi;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    WxMiniApi wxMiniApi;

    @Autowired
    UserService userService;

    @Autowired
    AuthMapper authMapper;

    @Value("${appId}")
    String appId;

    @Value("${appSecret}")
    String appSecret;

    private WxLoginStatus getWxLoginStatus(String code) throws Jscode2sessionException {
        JSONObject jsonObject = wxMiniApi.authCode2Session(appId, appSecret, code);
        if (jsonObject == null) {
            throw new RuntimeException("调用微信端授权认证接口错误");
        } else if (jsonObject.get("errcode") != null) {
            throw new Jscode2sessionException((Integer) jsonObject.get("errcode")
                    , (String) jsonObject.get("errmsg"));
        }

        String openId = jsonObject.getString("openid");
        String sessionKey = jsonObject.getString("session_key");
        WxLoginStatus wxLoginStatus = new WxLoginStatus();
        wxLoginStatus.setOpenId(openId);
        wxLoginStatus.setSessionKey(sessionKey);
        return wxLoginStatus;
    }


    /**
     * 为登录接口提供服务。
     * 检查用户是否注册，若未注册，为用户自动注册；若已注册，认证用户身份
     * @param code 微信临时登录凭证
     * @return 返回认证结果，若认证通过，返回用户信息和登录token（包含用户id）
     */
    @Override
    public Result authentication(String code) {

        //获取用户微信openId
        String openId = "";
        String sessionKey = "";

        try {
            //获取微信登录态
            WxLoginStatus wxLoginStatus = getWxLoginStatus(code);
            openId = wxLoginStatus.getOpenId();
            sessionKey = wxLoginStatus.getSessionKey();
        } catch (Jscode2sessionException ex) {
            if (ex.getErrcode().equals(40029)) {
                return new Result().codeEnum(ResultCodeEnum.OAUTH_CODE_INVALID);
            } else if (ex.getErrcode().equals(40163)) {
                return new Result().codeEnum(ResultCodeEnum.OAUTH_CODE_BEEN_USED);
            }
        }

        //判断用户表中是否存在该用户，不存在则进行解密得到用户信息，并进行新增用户
        UserVo resultUser = authMapper.authenticateUserByOpenId(openId);

        ResultCodeEnum resultCode = null;

        //用户没有注册，向数据库插入新用户，不返回token
        if (resultUser == null) {

            resultUser = new UserVo();
            resultUser.setOpenId(openId);
            resultUser.setSessionKey(sessionKey);
            resultUser.setInfoComplete(0);
            userService.save(resultUser);

            resultCode = ResultCodeEnum.USER_INFO_NOT_COMPLETE;
            log.debug("用户注册：" + ResultCodeEnum.USER_INFO_NOT_COMPLETE.toString());
        }
        //用户已经注册，判断是否完善了信息，是则返回token
        else {

            //如果用户已经完善信息，返回登录token
            if (resultUser.getInfoComplete() == 1) {
                Map<String, Object> claim = new HashMap<>();
                claim.put("id", resultUser.getId());
                String token = JwtHelper.createToken(claim);

                resultUser.setToken(token);

                resultCode = ResultCodeEnum.SUCCESS;
                log.debug("用户登录成功");
                log.debug("用户id:" + resultUser.getId());
            }
            //如果用户没有完善信息，就不返回登录token
            else {
                resultCode = ResultCodeEnum.USER_INFO_NOT_COMPLETE;
                log.debug(ResultCodeEnum.USER_INFO_NOT_COMPLETE.toString());
            }
        }

        Map<String, Object> resultData = BeanUtils.bean2Map(resultUser, new String[]{
                "openId",
                "sessionKey"
        });

        return Result.build(resultData, resultCode);
    }


    /**
     * 用户授权，为授权拦截器提供服务。
     * 检查用户的权限是否足以访问当前api
     * @param id 用户id
     * @param requestURL api的url
     * @param requestMethod api的请求方式
     * @return 如果权限足够，返回true，如果权限不足，返回false
     */
    @Override
    public boolean authorization(Long id, String requestURL, String requestMethod) {
        //查询用户权限url
        List<Permission> permissions = authMapper.getUserPermissionByUserId(id);

        //检查用户是否有权限
        for (Permission permission : permissions) {
            String[] str = permission.getUrl().split(" ");
            String allowedMethod = str[0];
            String allowedUrl = str[1];

            if (allowedUrl.equals(requestURL) && allowedMethod.toLowerCase().equals(requestMethod.toLowerCase())) {
                return true;
            }
        }

        //授权失败
        return false;
    }

}