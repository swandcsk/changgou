package com.changgou.pay.controller;

import com.changgou.pay.service.WeixinPayService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/weixin/pay")
public class WeixinPayController {

    @Autowired
    private WeixinPayService weixinPayService;


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
