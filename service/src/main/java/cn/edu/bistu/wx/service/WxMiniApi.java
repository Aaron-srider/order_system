package cn.edu.bistu.wx.service;


import com.alibaba.fastjson.JSONObject;

/**
 * 微信小程序统一服务端API接口
 * @author zhuhuix
 * @date 2020-04-03
 */
public interface WxMiniApi {

    /**
     * role.code2Session
     * https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/login/auth.code2Session.html
     * 请求参数   属性	     类型	   默认值	必填	 说明
     * @param   appId	     string		         是	   小程序 appId
     * @param   secret	     string		         是	   小程序 appSecret
     * @param   jsCode	     string		         是	   登录时获取的 code
     *          grantType	 string		         是	   授权类型，此处只需填写 authorization_code
     * 返回值
     * @return  JSON 数据包
     *           属性	     类型	   说明
     *          openid	     string	  用户唯一标识
     *          session_key	 string	  会话密钥
     *          unionid	     string	  用户在开放平台的唯一标识符，在满足 UnionID 下发条件的情况下会返回，详见 UnionID 机制说明。
     *          errcode	     number	  错误码
     *          errmsg	     string	  错误信息
     *
     *          errcode 的合法值
     *
     *          值	         说明	                     最低版本
     *          -1	         系统繁忙，此时请开发者稍候再试
     *          0	         请求成功
     *          40029	     code 无效
     *          45011	     频率限制，每个用户每分钟100次
     */
    JSONObject authCode2Session(String appId, String secret, String jsCode);

    /**
     * 获取小程序的accessToken接口，如果本地缓存了access-token且没有过期，直接从本地获取，否则访问微信接口。
     * 之所以要缓存到本地，是因为每天请求微信接口的次数是有限制的。
     *
     * @param appId  小程序appId
     * @param secret 小程序secret
     * @return 返回访问微信服务器的accessToken
     */
    String getAccessToken(String appId, String secret);

    /**
     *
     * @param sig 签名
     * @param times 时间戳
     * @param nonce 随机数
     * @return 验证通过返回true，否则false
     */
    boolean check(String sig, String times, String nonce);

    /**
     * 小程序发送模板消息给指定用户
     * @param openId 目标用户的openId
     */
    void sendSubscribeMsg(String openId);

}