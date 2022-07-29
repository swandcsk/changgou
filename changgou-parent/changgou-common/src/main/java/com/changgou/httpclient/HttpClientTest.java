package com.changgou.httpclient;

import entity.HttpClient;
import org.junit.Test;

import java.io.IOException;

/**
 * HttpClient使用案例
 */
public class HttpClientTest {
    /**
     * 发送http/https请求
     *  发送指定参数
     *  可以获取响应的结果
     */
    @Test
    public void testHttpClient() throws IOException {
        //https://api.mch.weixin.qq.com/pay/orderquery

        String url = "https://api.mch.weixin.qq.com/pay/orderquery";

        //创建HttpClient对象
        HttpClient httpClient = new HttpClient(url);

        //要发送的XML数据
        String xml = "<xml><name>张三</name></xml>";

        //设置请求的XML参数
        httpClient.setXmlParam(xml);

        //https/http
        httpClient.setHttps(true);

        //发送请求 POST请求
        httpClient.post();

        //获取响应数据
        String result = httpClient.getContent();

        System.out.println(result);
    }
}
