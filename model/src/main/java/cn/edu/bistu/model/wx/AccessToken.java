package cn.edu.bistu.model.wx;

import java.io.Serializable;
import java.util.Date;


public class AccessToken implements Serializable{

    /**
     * access_token
     */
    private String token;

    /**
     * access_token的过期时间戳，单位毫秒
     */
    private String expireAt;

    public AccessToken() {
    }

    /**
     * 将传入的expireIn加上当前时间戳，生成access_token过期时间戳expireAt
     * @param token 微信服务器发来的access_token
     * @param expireIn 微信服务器发来的access_token的过期时间
     */
    public AccessToken(String token, String expireIn) {
        this.token = token;
        Long s = Long.valueOf(expireIn) * 1000 + System.currentTimeMillis();
        this.expireAt = s + "";
    }

    /**
     * 判断access_token是否已经过期，如果当前时间戳已经大于过期时间戳，则access_token过期
     * @return 过期返回true；否则返回false
     */
    public boolean isExpired() {
        Long now = new Date().getTime() / 1000;
        return System.currentTimeMillis() >  Long.valueOf(expireAt);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getExpireAt() {
        return expireAt;
    }

    /**
     * 将传入的expireIn加上当前时间戳，生成access_token过期时间戳expireAt
     * @param expireIn 微信服务器发来的access_token的过期时间
     */
    public void setExpireIn(String expireIn) {
        Long s = Long.valueOf(expireIn) * 1000 + System.currentTimeMillis();
        this.expireAt = s + "";
    }

    @Override
    public String toString() {
        return "AccessToken{" +
                "token='" + token + '\'' +
                ", expireAt='" + expireAt + '\'' +
                '}';
    }
}
