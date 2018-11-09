package org.base.utils.http;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: jinxiang
 * @Date: 2018/11/7 16:29
 * @Description:
 */
public class HttpClientUtil {
    private static CloseableHttpClient httpClient;

    public HttpClientUtil() {
        HttpClientBuilder builder = HttpClientBuilder.create();

        //Httpclient连接池，长连接保持30秒
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(5, TimeUnit.SECONDS);

        //设置总连接数
        connectionManager.setMaxTotal(1000);
        //设置同路由的并发数
        connectionManager.setDefaultMaxPerRoute(1000);

        //设置header
        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; rv:36.0) Gecko/20100101 Firefox/36.04"));
        headers.add(new BasicHeader("Accept-Encoding", "gzip, deflate"));
        headers.add(new BasicHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3"));
        headers.add(new BasicHeader("Connection", "keep-alive"));

        //创建HttpClient
        httpClient = builder
                .setConnectionManager(connectionManager)
                .setDefaultHeaders(headers)
                .setRetryHandler(new DefaultHttpRequestRetryHandler(2, true)) //设置重试次数
                .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy()) //设置保持长连接
                .build();
    }

    /**
     * get body
     *
     * @param url
     * @param params
     * @return
     * @throws ParseException
     * @throws IOException
     */
    public CloseableHttpResponse get(String url, Map<String, String> params) throws ParseException, IOException {
        List<NameValuePair> nvs = new ArrayList<>();
        for (String key : params.keySet()) {
            nvs.add(new BasicNameValuePair(key, params.get(key)));
        }
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nvs, "UTF-8");
        HttpGet httpGet = new HttpGet(url + "?" + EntityUtils.toString(entity, Charset.defaultCharset()));
        return httpClient.execute(httpGet);
    }


    /**
     * post param
     *
     * @param url
     * @param params
     * @return
     * @throws IOException
     */
    public CloseableHttpResponse post(String url, Map<String, String> params) throws IOException {
        HttpPost post = new HttpPost(url);
        List<NameValuePair> nvs = new ArrayList<>();
        for (String key : params.keySet()) {
            nvs.add(new BasicNameValuePair(key, params.get(key)));
        }
        post.setHeader("Accept", "application/json");
        post.setHeader("Content-type", "application/json");
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nvs, "UTF-8");
        entity.setContentType("application/json");
        post.setEntity(entity);
        return httpClient.execute(post);
    }

    /**
     * post obj
     * @param url
     * @param obj
     * @return
     * @throws IOException
     */
    public CloseableHttpResponse post(String url, Object obj) throws IOException {
        HttpPost post = new HttpPost(url);
        List<NameValuePair> nvs = new ArrayList<>();
        Map<String, String> params = transBean2Map(obj);
        for (String key : params.keySet()) {
            nvs.add(new BasicNameValuePair(key, params.get(key)));
        }
        post.setHeader("Accept", "application/json");
        post.setHeader("Content-type", "application/json");
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nvs, "UTF-8");
        entity.setContentType("application/json");
        post.setEntity(entity);
        return httpClient.execute(post);
    }


    /**
     * post json
     *
     * @param url
     * @param json
     * @return
     * @throws IOException
     */
    public CloseableHttpResponse postJson(String url, String json) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        StringEntity entity = new StringEntity(json);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        return httpClient.execute(httpPost);
    }

    /**
     * 文件上传
     *
     * @param url
     * @param fileName
     * @param inputStream
     * @return
     * @throws IOException
     */
    public CloseableHttpResponse uploadFile(String url, String fileName, InputStream inputStream) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody(fileName, inputStream);
        HttpEntity multipart = builder.build();
        httpPost.setEntity(multipart);
        return client.execute(httpPost);
    }

    /**
     * 文件上传 with param
     *
     * @param url
     * @param fileName
     * @param inputStream
     * @return
     * @throws IOException
     */
    public CloseableHttpResponse uploadFileMultipart(String url, String fileName, InputStream inputStream, Map<String, String> params) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        for (String key : params.keySet()) {
            builder.addTextBody(key, params.get(key));
        }
        builder.addBinaryBody(fileName, inputStream);
        HttpEntity multipart = builder.build();
        httpPost.setEntity(multipart);
        return client.execute(httpPost);
    }

    public CloseableHttpResponse uploadFile(String url, String fileParamName, File file, Map<String, String> params) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//        builder.setCharset(Charset.forName(HTTP.UTF_8));
        for (String key : params.keySet()) {
            builder.addTextBody(key, params.get(key));
        }

        builder.addBinaryBody(fileParamName, file, ContentType.MULTIPART_FORM_DATA, file.getName());
        HttpEntity multipart = builder.build();
        httpPost.setEntity(multipart);
        return client.execute(httpPost);
    }


    /**
     * put param
     *
     * @param url
     * @param params
     * @return
     * @throws IOException
     */
    public CloseableHttpResponse put(String url, Map<String, String> params)
            throws IOException {
        HttpPut put = new HttpPut(url);
        List<NameValuePair> nvs = new ArrayList<>();
        for (String key : params.keySet()) {
            nvs.add(new BasicNameValuePair(key, params.get(key)));
        }
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nvs, "UTF-8");
        put.setEntity(entity);
        return httpClient.execute(put);
    }

    /**
     * Bean --> Map 1: 利用Introspector和PropertyDescriptor 将Bean --> Map
     *
     * @param obj
     * @return
     */
    public static Map<String, String> transBean2Map(Object obj) {
        if (obj == null) {
            return null;
        }
        Map<String, String> map = new HashMap<>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();

                // 过滤class属性
                if (!key.equals("class")) {
                    // 得到property对应的getter方法
                    Method getter = property.getReadMethod();
                    // 转String
                    String value = getter.invoke(obj).toString();
                    map.put(key, value);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("java bean转map异常");
        }
        return map;
    }

}
