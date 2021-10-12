package cn.edu.bistu.auth.service;

import cn.edu.bistu.admin.User.Service.UserService;
import cn.edu.bistu.admin.User.mapper.UserDao;
import cn.edu.bistu.auth.JwtHelper;
import cn.edu.bistu.auth.exception.Jscode2sessionException;
import cn.edu.bistu.auth.mapper.AuthMapper;
import cn.edu.bistu.auth.mapper.UserMapper;
import cn.edu.bistu.common.exception.*;
import cn.edu.bistu.common.utils.UserUtils;
import cn.edu.bistu.constants.ResultCodeEnum;
import cn.edu.bistu.constants.Role;
import cn.edu.bistu.model.common.result.DaoResult;
import cn.edu.bistu.model.WxLoginStatus;
import cn.edu.bistu.model.common.result.ServiceResult;
import cn.edu.bistu.model.common.result.ServiceResultImpl;
import cn.edu.bistu.model.entity.auth.Permission;
import cn.edu.bistu.model.entity.auth.User;
import cn.edu.bistu.model.entity.auth.UserRole;
import cn.edu.bistu.model.vo.UserVo;
import cn.edu.bistu.wx.service.WxMiniApi;
import cn.edu.bistu.wx.service.WxMiniApiImpl;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
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
    UserUtils userUtils;

    @Autowired
    UserDao userDao;

    @Autowired
    UserService userService;

    @Autowired
    WxMiniApi wxMiniApi;

    @Autowired
    AuthMapper authMapper;

    @Autowired
    UserMapper userMapper;

    @Value("${mini-appId}")
    String miniAppId;

    @Value("${mini-appSecret}")
    String miniAppSecret;

    @Value("${adminSystem-appId}")
    String adminSystemAppId;

    @Value("${adminSystem-appSecret}")
    String adminSystemAppSecret;


    private WxLoginStatus getWxLoginStatus(String code) throws Jscode2sessionException {
        JSONObject jsonObject = wxMiniApi.authCode2Session(miniAppId, miniAppSecret, code);
        if (jsonObject == null) {
            throw new RuntimeException("调用微信端授权认证接口错误");
        } else if (jsonObject.get("errcode") != null) {
            throw new Jscode2sessionException((Integer) jsonObject.get("errcode")
                    , (String) jsonObject.get("errmsg"));
        }

        String openId = jsonObject.getString("openid");
        String sessionKey = jsonObject.getString("session_key");
        String unionId = jsonObject.getString("unionid");
        WxLoginStatus wxLoginStatus = new WxLoginStatus();
        wxLoginStatus.setOpenId(openId);
        wxLoginStatus.setSessionKey(sessionKey);
        wxLoginStatus.setUnionId(unionId);
        return wxLoginStatus;
    }

    private WxLoginStatus getAdminWxLoginStatus(String code) throws Jscode2sessionException {
        JSONObject jsonObject = wxMiniApi.GetUnionIdForThirdPartyWebSites(adminSystemAppId, adminSystemAppSecret, code);
        if (jsonObject == null) {
            throw new RuntimeException("调用微信端授权认证接口错误");
        } else if (jsonObject.get("errcode") != null) {
            throw new Jscode2sessionException((Integer) jsonObject.get("errcode")
                    , (String) jsonObject.get("errmsg"));
        }

        String openId = jsonObject.getString("openid");
        String sessionKey = jsonObject.getString("session_key");
        String unionId = jsonObject.getString("unionid");
        log.debug("access_token unionId: " + unionId);
        WxLoginStatus wxLoginStatus = new WxLoginStatus();
        wxLoginStatus.setOpenId(openId);
        wxLoginStatus.setSessionKey(sessionKey);
        wxLoginStatus.setUnionId(unionId);
        return wxLoginStatus;
    }


    /**
     * 为登录接口提供服务
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
        String unionId = "";

        try {
            //获取微信登录态
            WxLoginStatus wxLoginStatus = getWxLoginStatus(code);
            openId = wxLoginStatus.getOpenId();
            sessionKey = wxLoginStatus.getSessionKey();
            unionId = wxLoginStatus.getUnionId();
        } catch (Jscode2sessionException ex) {
            if (ex.getErrcode().equals(40029)) {
                throw new ResultCodeException("code:" + code, ResultCodeEnum.OAUTH_CODE_INVALID);
            } else if (ex.getErrcode().equals(40163)) {
                throw new ResultCodeException("code:" + code, ResultCodeEnum.OAUTH_CODE_BEEN_USED);
            }
        }

        //判断用户表中是否存在该用户，不存在则进行解密得到用户信息，并进行新增用户
        DaoResult<User> daoResult = userDao.getOneUserByOpenId(openId);

        User resultUser = daoResult.getResult();

        //用户没有注册，向数据库插入新用户，不返回token
        if (resultUser == null) {
            registerUser(openId, sessionKey, unionId);
        }
        //用户已经注册，判断是否完善了信息，是则返回token
        else {

            //判断用户是否锁定
            Integer isLock = resultUser.getIsLock();
            if (isLock.equals(1)) {
                throw new ResultCodeException(resultUser, ResultCodeEnum.USER_LOCK);
            }

            //判断用户信息是否已经完善
            Integer infoComplete = resultUser.getInfoComplete();
            if (infoComplete.equals(1)) {
                String token = generateUserToken(resultUser.getId());
                daoResult.addDetailInfo("token", token);
            }
            //如果用户没有完善信息，就不返回登录token
            else {
                resultUser.setUnionId(null);
                resultUser.setOpenId(null);
                resultUser.setSessionKey(null);
                throw new ResultCodeException(resultUser, ResultCodeEnum.USER_INFO_NOT_COMPLETE);
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
            if (index != -1) {
                allowedUrl = allowedUrl.substring(0, index);
                index = index > requestURL.length() ? requestURL.length() : index;
                requestURL = requestURL.substring(0, index);
            }

            if (allowedUrl.equals(requestURL) && allowedMethod.toLowerCase().equals(requestMethod.toLowerCase())) {
                return true;
            }
        }

        //授权失败
        return false;
    }

    private String generateUserToken(Long userId) {
        Map<String, Object> claim = new HashMap<>();
        claim.put("id", userId);
        String token = JwtHelper.createToken(claim);
        return token;
    }


    private Map<Long, Object> forgeToken(Long[] userIds) {
        Map<Long, Object> map = new HashMap<>();
        for (Long userId : userIds) {
            String token = generateUserToken(userId);
            map.put(userId, token);
        }
        return map;
    }

    @Override
    public ServiceResult<JSONObject> userInfoCompletion(UserVo userVo) {
        Long roleId = userVo.getRoleId();

        DaoResult<User> daoResult = userDao.getOneUserById(userVo.getId());
        User user = daoResult.getResult();

        //用户没注册
        if (user == null) {
            throw new ResultCodeException(user, ResultCodeEnum.USER_NOT_REGISTERED);
        }

        Integer infoComplete = user.getInfoComplete();
        ////用户已经完善过信息
        //if (infoComplete.equals(1)) {
        //    throw new ResultCodeException(user, ResultCodeEnum.USER_INFO_COMPLETED);
        //}


        userVo.setInfoComplete(1);

        ServiceResult<JSONObject> serviceResult = userService.updateUser(userVo);

        //向UserRole表中插入数据
        improveUserRoleInfo(roleId, userVo.getId());
        return serviceResult;
    }


    /**
     * 后台管理系统用户认证，为登录接口提供服务。该接口不对数据库做任何修改（不提供注册服务）。
     * 使用code换取unionid，直接根据unionid查表检查是否存在用户，若存在且完善信息且角色为管理员，返回用户信息和登录token（包含用户id）；
     * 如果存在但是没有完善信息，返回错误代码；
     * 若不存在，直接返回未注册错误码，不自动注册用户；
     *
     * @param code 第三方网站微信临时登录凭证
     * @return 返回认证结果，若认证通过，返回用户信息和登录token（包含用户id）
     */
    @Override
    public ServiceResult adminSystemAuthentication(String code) {
        //获取用户微信openId
        String unionId = "";

        try {
            //获取微信登录态
            WxLoginStatus wxLoginStatus = getAdminWxLoginStatus(code);
            unionId = wxLoginStatus.getUnionId();
        } catch (Jscode2sessionException ex) {
            if (ex.getErrcode().equals(40029)) {
                throw new ResultCodeException("code:" + code, ResultCodeEnum.OAUTH_CODE_INVALID);
            } else if (ex.getErrcode().equals(40163)) {
                throw new ResultCodeException("code:" + code, ResultCodeEnum.OAUTH_CODE_BEEN_USED);
            }
        }

        DaoResult<User> oneUserByUnionId = userDao.getOneUserByUnionId(unionId);


        User user = oneUserByUnionId.getResult();

        log.debug("result: " + oneUserByUnionId.getResult());

        log.debug("result: " + oneUserByUnionId.getDetailInfo());

        //用户没有注册
        if(user==null) {
            throw new ResultCodeException(user, ResultCodeEnum.USER_NOT_REGISTERED);
        }

        log.debug("userRegistered");

        //用户是否完善信息
        if (user.getInfoComplete().equals(0)) {
            throw new ResultCodeException(user, ResultCodeEnum.USER_INFO_NOT_COMPLETE);
        }

        log.debug("userInfoComplete");

        //判断是否是管理员
        boolean isAdmin = userService.isAdmin(user.getId());
        if (!isAdmin) {
            throw new ResultCodeException(user, ResultCodeEnum.HAVE_NO_RIGHT);
        }

        log.debug("user is an admin");

        //判断用户是否锁定
        Integer isLock = user.getIsLock();
        if (isLock.equals(1)) {
            throw new ResultCodeException(user, ResultCodeEnum.USER_LOCK);
        }

        log.debug("user account valid");

        String token = generateUserToken(user.getId());
        oneUserByUnionId.addDetailInfo("token", token);

        log.debug("token:" + token);

        return new ServiceResultImpl(oneUserByUnionId.getValue());
    }


    @Test
    public void getOpenIdAndUnionIdByTrick() {
        Map<String, Object> map = new HashMap<>();
        map.put("韩欣怡", "053CEl100bsdAM1U6c200vkVwj3CEl1A");
        //map.put("姓名", "");
        //map.put("姓名", "");

        for (String name : map.keySet()) {
            String code = (String) map.get(name);

            JSONObject jsonObject = new WxMiniApiImpl().authCode2Session("wxbc043e13b23bfec6", "1725148a11cbdd403435138295080768", code);
            if (jsonObject == null) {
                throw new RuntimeException("调用微信端授权认证接口错误");
            } else if (jsonObject.get("errcode") != null) {
                throw new Jscode2sessionException((Integer) jsonObject.get("errcode")
                        , (String) jsonObject.get("errmsg"));
            }

            String openId = jsonObject.getString("openid");
            String sessionKey = jsonObject.getString("session_key");
            String unionId = jsonObject.getString("unionid");
            WxLoginStatus wxLoginStatus = new WxLoginStatus();
            wxLoginStatus.setOpenId(openId);
            wxLoginStatus.setSessionKey(sessionKey);
            wxLoginStatus.setUnionId(unionId);

            map.put(name, wxLoginStatus);
        }

        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(map));

        System.out.println(jsonObject);

    }

    @Test
    public void forgeToken() {
        Map<Long, Object> tokens = forgeToken(new Long[]{
                2L, 3L
        });

        System.out.println(tokens);
    }

    /**
     * 如果该用户没有角色，插入新的roleId，如果已有角色，更改该角色。上述角色中排除管理员和业务员。
     * @param roleId 更新的角色id
     * @param userId 更新的用户id
     */
    private void improveUserRoleInfo(Long roleId, Long userId) {
        //找出不是管理员的角色关系，更新它
        boolean flag=false;
        UserRole targetUserRole=null;
        List<UserRole> userRoleList = userDao.getUserRoleMapper().selectList(new QueryWrapper<UserRole>().eq("user_id", userId));
        for (UserRole userRole : userRoleList) {
            if(userRole.getRoleId() != userUtils.convertRoleConstant2Entity(Role.ADMIN).getId() &&
                    userRole.getRoleId() != userUtils.convertRoleConstant2Entity(Role.OPERATOR).getId() ) {
                flag=true;
                targetUserRole=userRole;
                break;
            }
        }
        if(flag) {
            targetUserRole.setRoleId(roleId);
            userDao.getUserRoleMapper().updateById(targetUserRole);
        } else {
            //插入角色
            UserRole userRole = new UserRole();
            userRole.setRoleId(roleId);
            userRole.setUserId(userId);
            userDao.getUserRoleMapper().insert(userRole);
        }

    }

    private void registerUser(String openid, String sessionkey, String unionId) {
        User user = new User();
        user.setOpenId(openid);
        user.setSessionKey(sessionkey);
        user.setUnionId(unionId);
        user.setInfoComplete(0);
        userDao.getUserMapper().insert(user);
        user.setUnionId(null);
        user.setOpenId(null);
        user.setSessionKey(null);
        throw new ResultCodeException(user, ResultCodeEnum.USER_INFO_NOT_COMPLETE);

    }

}