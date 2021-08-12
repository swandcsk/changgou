package com.changgou.oauth.interceptor;

import com.changgou.oauth.util.AdminToken;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TokenRequestInterceptor implements RequestInterceptor {

    /**
     * Feign执行之前,进行拦截
     * @param template
     */
    @Override
    public void apply(RequestTemplate template) {
        /**
         * 1.没有令牌,生成令牌(admin)
         * 2.令牌需要携带过去
         * 3.令牌需要存放到Header中
         * 4.请求->Feign调用->拦截器RequestInterceptor->Feign调用之前拦截
         */
        //生成admin令牌
        String token = AdminToken.adminToken();
        template.header("Authorization","bearer " + token);
    }
}
