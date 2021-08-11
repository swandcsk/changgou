package com.changgou.filter;

import com.changgou.util.JwtUtil;
import io.netty.util.internal.StringUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 全局过滤器
 * 实现用户权限鉴别(校验)
 */
@Component
public class AuthorizeFilter implements GlobalFilter, Ordered {
    //令牌的名字
    private static final String AUTHORIZE_TOKEN = "Authorization";

    /**
     * 全局拦截
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        //获取用户令牌信息
        // 1)请求头中
        String token = request.getHeaders().getFirst(AUTHORIZE_TOKEN);
        //boolean true:令牌在头文件中 false:令牌不在头中 ->将令牌封装到头中,再传递给其他微服务
        boolean hasToken = true;

        // 2)参数获取令牌
        if(StringUtils.isEmpty(token)){
            token = request.getQueryParams().getFirst(AUTHORIZE_TOKEN);
            hasToken = false;
        }

        // 3)cookie中
        if(StringUtils.isEmpty(token)){
            HttpCookie cookie = request.getCookies().getFirst(AUTHORIZE_TOKEN);
            if(cookie != null){
                token = cookie.getValue();
            }
        }
        //如果没有令牌,则拦截
        if(StringUtils.isEmpty(token)){
            //设置没有权限的状态码 401
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            //响应空数据
            return response.setComplete();
        }
        //令牌判断是否为空，如果不为空，将令牌放到头文件中，放行


        //如果有令牌,则校验令牌是否有效
        //try {
            //JwtUtil.parseJWT(token);
        //} catch (Exception e) {
         //   e.printStackTrace();
            //无效拦截
        //}
        //令牌为空，则不允许访问，直接拦截 bearer
        if(StringUtils.isEmpty(token)){
            //设置没有权限的状态码 401
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            //响应空数据
            return response.setComplete();
        }else{
            //判断当前令牌是否有bearer前缀，如果没有，则添加前缀bearer
            if(!token.startsWith("bearer ")&&!token.startsWith("Bearer ")){
                token = "bearer " + token;
            }
        }
        if(!hasToken){
            //将令牌封装到头文件中
            request.mutate().header(AUTHORIZE_TOKEN,token);
        }
        //有效放行
        return chain.filter(exchange);
    }

    /**
     * 排序
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
