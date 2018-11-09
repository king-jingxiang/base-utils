package org.base.utils.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.base.utils.mapper.JacksonUtil;
import org.base.utils.mapper.JsonMapper;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class HttpClientUtilTest {
    HttpClientUtil httpClientUtil = new HttpClientUtil();
    @Test
    public void get() throws IOException {
        CloseableHttpResponse closeableHttpResponse = httpClientUtil.get("https://service.interface.bitahub.com/article/gpu/getBannerGpuInfo", new HashMap<>());
        HttpEntity entity = closeableHttpResponse.getEntity();
        String result = EntityUtils.toString(entity);

        System.out.println(result);
    }

    @Test
    public void post() throws Exception{
        Map<String,String> param=new HashMap<>();
        param.put("email","ruanxingbaozi@dingtalk.com");
        param.put("password","P@ssw0rd");
        HttpResponse post = httpClientUtil.post("https://service.interface.bitahub.com/manage/login", param);
        String result = EntityUtils.toString(post.getEntity());

        JsonMapper jsonMapper = new JsonMapper();
        Map map = jsonMapper.toMap(result);
        System.out.println(map);
        System.out.println(result);

    }

    @Test
    public void put() {
    }

}
