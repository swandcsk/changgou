package com.changgou.order.controller;

import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.CartService;
import com.mysql.cj.protocol.ResultListener;
import entity.Result;
import entity.StatusCode;
import entity.TokenDecode;
import jdk.internal.org.objectweb.asm.tree.analysis.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/cart")
public class CartController {


    @Autowired
    private CartService cartService;

    /**
     * 加入购物车
     * 1,加入购物车的数量
     * 2,商品ID
     */
    @GetMapping(value = "/add")
    public Result add(Integer num,Long id){
        Map<String, String> userInfo = TokenDecode.getUserInfo();
        System.out.println(userInfo);
        String username = userInfo.get("username");

        cartService.add(num,id,username);
        return new Result(true, StatusCode.OK,"加入购物车成功!");
    }


    /**
     * 购物车列表
     */
    @GetMapping(value = "/list")
    public Result<List<OrderItem>> list (){
        //用户的令牌信息->解析令牌信息->username
        //Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails)SecurityContextHolder.getContext().getAuthentication().getDetails();
//        String token = details.getTokenValue();

        Map<String, String> userInfo = TokenDecode.getUserInfo();
        System.out.println(userInfo);
        String username = userInfo.get("username");
        //获取用户登录名
        //String username = "szitheima";
        List<OrderItem> orderItemList = cartService.list(username);
        return new Result<List<OrderItem>>(true,StatusCode.OK,"购物车列表查询成功",orderItemList);

    }

}
