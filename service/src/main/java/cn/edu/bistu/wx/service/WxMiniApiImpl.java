package cn.edu.bistu.wx.service;

import cn.edu.bistu.auth.WeChatUtil;
import cn.edu.bistu.model.wx.AccessToken;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * 微信小程序Api接口实现类
 *
 * @author zhuhuix
 * @date 2020-04-03
 */

@Slf4j
@Service
public class WxMiniApiImpl implements WxMiniApi {

    public static final String appid = "wxbc043e13b23bfec6";
    public static final String secret = "1725148a11cbdd403435138295080768";
    public static final String TOKEN = "123456";

    @Override
    public JSONObject authCode2Session(String appId, String secret, String jsCode) {

        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appId + "&secret=" + secret + "&js_code=" + jsCode + "&grant_type=authorization_code";
        String str = WeChatUtil.httpRequest(url, "GET", null);
        log.info("api/wx-mini/getSessionKey:" + str);
        if (StringUtils.isEmpty(str)) {
            return null;
        } else {
            return JSONObject.parseObject(str);
        }

    }

    /**
     * 获取小程序的accessToken接口，如果本地缓存了access-token且没有过期，直接从本地获取，否则访问微信接口。
     * 之所以要缓存到本地，是因为每天请求微信接口的次数是有限制的。
     *
     * @param appId  小程序appId
     * @param secret 小程序secret
     * @return 返回访问微信服务器的accessToken
     */
    @Override
    public String getAccessToken(String appId, String secret) {


        File file = new File("access_token");


        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(new FileInputStream(file));
        } catch (StreamCorruptedException | EOFException ex) {
            String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appId + "&secret=" + secret;
            String str = WeChatUtil.httpRequest(url, "GET", null);
            JSONObject jsonObject = JSONObject.parseObject(str);
            String token = jsonObject.getString("access_token");
            String expiresIn = jsonObject.getString("expires_in");
            AccessToken accessToken = new AccessToken();
            accessToken.setToken(token);
            accessToken.setExpireIn(expiresIn);

            ObjectOutputStream out = null;
            try {
                out = new ObjectOutputStream(new FileOutputStream(file));
                out.writeObject(accessToken);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return token;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        AccessToken accessToken = null;
        try {
            accessToken = (AccessToken) in.readObject();

            if (accessToken.isExpired()) {
                String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appId + "&secret=" + secret;
                String str = WeChatUtil.httpRequest(url, "GET", null);
                JSONObject jsonObject = JSONObject.parseObject(str);
                String token = jsonObject.getString("access_token");
                String expiresIn = jsonObject.getString("expires_in");
                accessToken.setToken(token);
                accessToken.setExpireIn(expiresIn);

                ObjectOutputStream out = null;
                try {
                    out = new ObjectOutputStream(new FileOutputStream(file));
                    out.writeObject(accessToken);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return token;
            } else {
                return accessToken.getToken();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void sendSubscribeMsg(String openId) {
        String httpBody = "{    \n" +
                "            \"touser\": \""+openId+"\",\n" +
                "            \"template_id\": \"N4xxLi6-KWyqh1dwhHkPJ5CRDvcz9fugvjfH0VsJrJY\",\n" +
                "            \"page\": \"index\",\n" +
                "            \"miniprogram_state\": \"developer\",\n" +
                "            \"lang\": \"zh_CN\",\n" +
                "            \"data\": {\n" +
                "                \"name1\": {\n" +
                "                    \"value\": \"张三\"\n" +
                "                },\n" +
                "                \"time2\": {\n" +
                "                    \"value\": \"2015年01月05日\"\n" +
                "                },\n" +
                "                \"thing3\": {\n" +
                "                    \"value\": \"测试备注\"\n" +
                "                }\n" +
                "            }\n" +
                "        }";

        String token = getAccessToken(appid, secret);
        String url = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=" + token;
        JSONObject jsonObject = JSONObject.parseObject(httpBody);

        String result = WeChatUtil.httpPost(url, jsonObject);
        log.debug(result);
    }


    @Override
    public boolean check(String sig, String times, String nonce)  {

        String[] arr = new String[]{TOKEN, times, nonce};
        Arrays.sort(arr);

        String str = arr[0] + arr[1] + arr[2];

        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("sha1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        byte[] digest = messageDigest.digest(str.getBytes());

        StringBuilder stringBuilder = new StringBuilder();

        char[] chars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        for (byte b : digest) {
            stringBuilder.append(chars[(b >> 4) & 15]);
            stringBuilder.append(chars[b & 15]);
        }

        String mySig = stringBuilder.toString();

        return mySig.equalsIgnoreCase(sig);
    }



}