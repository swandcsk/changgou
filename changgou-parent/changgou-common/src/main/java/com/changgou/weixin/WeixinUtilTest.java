package com.changgou.weixin;

import com.github.wxpay.sdk.WXPayUtil;
import org.junit.Test;

import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信SDK相关测试
 */
public class WeixinUtilTest {

    /**
     * 1生成随机字符
     * 2将Map转成XML字符串
     * 3将Map转成XML字符串，并生成签名
     * 4将XML字符串转成Map
     */
    @Test
    public void testDemo() throws Exception {
        //随机字符串
        String str = WXPayUtil.generateNonceStr();
        System.out.println("随机字符串：" + str);

        //将Map转成XML字符串
        Map<String,String> dataMap = new HashMap<>();
        dataMap.put("id","No.001");
        dataMap.put("title","畅购商城杯具支付");
        dataMap.put("money","998");


        String xmlstr = WXPayUtil.mapToXml(dataMap);
        System.out.println("XML字符串：\n"+xmlstr);


        //将Map转成XML字符串，并生成签名
        String signermxlStr = WXPayUtil.generateSignedXml(dataMap,"itcast");
        System.out.println("XML字符串带有签名：\n" + signermxlStr);


        //将XML字符串转成Map
        Map<String, String> mapResult = WXPayUtil.xmlToMap(signermxlStr);
        System.out.println("XML转成Map:\n"+mapResult);
    }
}
