package cn.edu.bistu.auth.service;

import cn.edu.bistu.User.mapper.UserDao;
import cn.edu.bistu.auth.JwtHelper;
import cn.edu.bistu.auth.exception.Jscode2sessionException;
import cn.edu.bistu.auth.mapper.AuthMapper;
import cn.edu.bistu.auth.mapper.UserMapper;
import cn.edu.bistu.common.exception.CodeBeenUsedException;
import cn.edu.bistu.common.exception.CodeInvalidException;
import cn.edu.bistu.common.exception.UserInfoNotCompleteException;
import cn.edu.bistu.common.exception.UserNotRegisteredException;
import cn.edu.bistu.constants.ResultCodeEnum;
import cn.edu.bistu.model.common.DaoResult;
import cn.edu.bistu.model.common.Result;
import cn.edu.bistu.model.WxLoginStatus;
import cn.edu.bistu.model.common.ServiceResult;
import cn.edu.bistu.model.common.ServiceResultImpl;
import cn.edu.bistu.model.entity.auth.Permission;
import cn.edu.bistu.model.entity.auth.User;
import cn.edu.bistu.model.entity.auth.UserRole;
import cn.edu.bistu.model.vo.UserVo;
import cn.edu.bistu.wx.service.WxMiniApi;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    UserDao userDao;

    @Autowired
    WxMiniApi wxMiniApi;

    @Autowired
    AuthMapper authMapper;

    @Autowired
    UserMapper userMapper;

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
     *
     * @param code 微信临时登录凭证
     * @return 返回认证结果，若认证通过，返回用户信息和登录token（包含用户id）
     */
    @Override
    public ServiceResult<JSONObject> authentication(String code) {

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
                throw new CodeInvalidException("code:" + code, ResultCodeEnum.OAUTH_CODE_INVALID);
            } else if (ex.getErrcode().equals(40163)) {
                throw new CodeBeenUsedException("code:" + code, ResultCodeEnum.OAUTH_CODE_BEEN_USED);
            }
        }

        //判断用户表中是否存在该用户，不存在则进行解密得到用户信息，并进行新增用户
        DaoResult<User> daoResult = userDao.getOneUserByOpenId(openId);

        User resultUser = daoResult.getResult();

        //用户没有注册，向数据库插入新用户，不返回token
        if (resultUser == null) {
            registerUser(openId, sessionKey);
        }
        //用户已经注册，判断是否完善了信息，是则返回token
        else {
            resultUser.setOpenId(null);
            resultUser.setSessionKey(null);
            Integer infoComplete = resultUser.getInfoComplete();
            //如果用户已经完善信息，返回登录token
            if (infoComplete.equals(1)) {
                Map<String, Object> claim = new HashMap<>();
                Long id = resultUser.getId();
                claim.put("id", id);
                String token = JwtHelper.createToken(claim);

                daoResult.addDetailInfo("token", token);
            }
            //如果用户没有完善信息，就不返回登录token
            else {
                throw new UserInfoNotCompleteException("user id:" + resultUser.getId(), ResultCodeEnum.USER_INFO_NOT_COMPLETE);
            }
        }

        ServiceResult<JSONObject> serviceResult = new ServiceResultImpl<>((JSONObject) daoResult.getValue());

        return serviceResult;
    }


    /**
     * 用户授权，为授权拦截器提供服务。
     * 检查用户的权限是否足以访问当前api
     *
     * @param id            用户id
     * @param requestURL    api的url
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

            int index = allowedUrl.lastIndexOf("/*");
            if(index != -1) {
                requestURL = requestURL.substring(0, index);
                allowedUrl = allowedUrl.substring(0, index);
            }

            if (allowedUrl.equals(requestURL) && allowedMethod.toLowerCase().equals(requestMethod.toLowerCase())) {
                return true;
            }
        }

        //授权失败
        return false;
    }

    public Map<Long, Object> forgeToken(Long[] userIds) {
        Map<Long, Object> map = new HashMap<>();

        for (Long userId : userIds) {
            Map<String, Object> claim = new HashMap<>();
            claim.put("id", userId);
            String token = JwtHelper.createToken(claim);
            map.put(userId, token);
        }
        return map;
    }

    @Override
    public void userInfoCompletion(UserVo userVo) {
        //查询数据库
        Long roleId = userVo.getRoleId();

        DaoResult<User> daoResult = userDao.getOneUserById(userVo.getId());
        User user = daoResult.getResult();

        //用户没注册
        if (user == null) {
            throw new UserNotRegisteredException("user id: " + userVo.getId(), ResultCodeEnum.USER_NOT_REGISTERED);
        }

        Integer infoComplete = user.getInfoComplete();
        //用户已经完善过信息
        if (infoComplete.equals(1)) {
            throw new UserNotRegisteredException("user id: " + userVo.getId(), ResultCodeEnum.USER_INFO_COMPLETED);
        }

        //更新用户表
        userDao.getUserMapper().userInfoComplete(userVo);

        //向UserRole表中插入数据
        improveUserRoleInfo(roleId, userVo.getId());
    }

    @Test
    public void forgeToken() {
        Map<Long, Object> tokens = forgeToken(new Long[]{
                1L
        });

        System.out.println(tokens);

    }

    private void improveUserRoleInfo(Long roleId, Long userId) {
        UserRole userRole = new UserRole();
        userRole.setRoleId(roleId);
        userRole.setUserId(userId);
        userDao.getUserRoleMapper().insert(userRole);

    }

    private void registerUser(String openid, String sessionkey) {
        User user = new User();
        user.setOpenId(openid);
        user.setSessionKey(sessionkey);
        user.setInfoComplete(0);
        userDao.getUserMapper().insert(user);
        throw new UserInfoNotCompleteException("user id:" + user.getId(), ResultCodeEnum.USER_INFO_NOT_COMPLETE);

    }

}