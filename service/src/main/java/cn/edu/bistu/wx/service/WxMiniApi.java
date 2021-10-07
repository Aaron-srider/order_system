package cn.edu.bistu.wx.service;


import com.alibaba.fastjson.JSONObject;

/**
 * 微信小程序统一服务端API接口
 *
 * @author zhuhuix
 * @date 2020-04-03
 */
public interface WxMiniApi {

    /**
     * 由code换取用户openid和session_key以及unionid
     * 请求参数   属性	     类型	   默认值	必填	 说明
     */
    JSONObject authCode2Session(String appId, String secret, String jsCode);

    /**
     * 由code换取用户openid和session_key以及unionid
     * 请求参数   属性	     类型	   默认值	必填	 说明
     */
    JSONObject GetUnionIdForThirdPartyWebSites(String appId, String secret, String jsCode);

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
     * 用于开发者服务器接入微信服务器时的签名验证
     *
     * @param sig   签名
     * @param times 时间戳
     * @param nonce 随机数
     * @return 验证通过返回true，否则false
     */
    boolean check(String sig, String times, String nonce);

    /**
     * 小程序发送模板消息给指定用户
     *
     * @param openId 目标用户的openId
     */
    void sendSubscribeMsg(String openId);

}