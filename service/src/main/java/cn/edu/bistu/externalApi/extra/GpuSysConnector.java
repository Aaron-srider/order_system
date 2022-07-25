package cn.edu.bistu.externalApi.extra;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class GpuSysConnector {
    @Value("${gpu-system.baseUrl}")
    private String baseUrl;
    @Value("${gpu-system.apiKey}")
    private String apiKey;


    public Response request(String method, String url, String body) throws IOException {

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody requestBody = body == null ? null : RequestBody.create(mediaType, body);

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(this.baseUrl + url)
                .method(method, requestBody)
                .addHeader("API-KEY", this.apiKey)
                .build();

        return client.newCall(request).execute();
    }

    public Response getRequest(String url) throws IOException{
        return request("GET", url ,null);
    }

    public Response postRequest(String url, String json) throws IOException{
        return request("POST", url ,json);
    }

    public Response putRequest(String url, String json) throws IOException{
        return request("PUT", url ,json);
    }

    public Response deleteRequest(String url) throws IOException{
        return request("DELETE", url ,null);
    }
}

