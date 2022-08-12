package com.changgou.pay.controller;

import com.alibaba.fastjson.JSON;
import com.changgou.pay.service.WeixinPayService;
import com.github.wxpay.sdk.WXPayUtil;
import entity.Result;
import entity.StatusCode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping(value = "/weixin/pay")
public class WeixinPayController {

    @Autowired
    private WeixinPayService weixinPayService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 支付结果通知回调方法
     */
    @RequestMapping(value = "/notify/url")
    public String notifyurl(HttpServletRequest request) throws Exception {
        //获取网络输入流
        ServletInputStream is = request.getInputStream();

        //创建一个OutputStream->输入文件中
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len=0;
        while((len=is.read(buffer))!=-1){
            baos.write(buffer,0,len);
        }

        //微信支付结果的字节数据
        byte[] bytes = baos.toByteArray();

        String xmlresult = new String(bytes, "UTF-8");
        System.out.println(xmlresult);

        //XML字符串->Map
        Map<String, String> resultMap = WXPayUtil.xmlToMap(xmlresult);
        System.out.println(resultMap);

        //发送支付结果给mq
        rabbitTemplate.convertAndSend("exchange.order","queue.order", JSON.toJSONString(resultMap));

        String result = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
        return result;
    }

    /**
     * 微信支付状态查询
     */
    @GetMapping(value = "/status/query")
    public Result queryStatus(String outtradeno){
        //查询支付状态
        Map map = weixinPayService.queryStatus(outtradeno);
        return new Result(true, StatusCode.OK,"查询支付状态成功！",map);
    }


    /**
     * 创建支付二维码
     */
    @RequestMapping(value = "/create/native")
    public Result createNative(@RequestParam Map<String,String> parameterMap){
        //创建二维码
        Map resultMap = weixinPayService.createnative(parameterMap);
        return new Result(true, StatusCode.OK,"创建二维码预付订单成功！",resultMap);
    }
}
