package com.itheima.jwt;

import io.jsonwebtoken.*;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 令牌的生成和解析
 */
public class JwtTest {

    /**
     * 创建令牌
     */
    @Test
    public void testCreateToken(){
        //构建Jwt令牌的对象
        JwtBuilder builder = Jwts.builder();
        builder.setIssuer("黑马");//颁发者
        builder.setIssuedAt(new Date());//颁发时间
        builder.setExpiration(new Date(System.currentTimeMillis()+3600000));//过期时间
        builder.setSubject("JWT令牌测试");//主题信息

        //自定义载荷信息
        Map<String,Object> userInfo = new HashMap<>();
        userInfo.put("company","黑马训练营");
        userInfo.put("adress","中南海");
        userInfo.put("money","3500");

        builder.addClaims(userInfo);//添加载荷
        builder.signWith(SignatureAlgorithm.HS256,"itcast");//1:签名算法2：密钥
        String token =  builder.compact();
        System.out.println(token);
    }

    /**
     * 解析令牌
     */
    @Test
    public void parseToken(){
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiLpu5HpqawiLCJpYXQiOjE2Mjc5NzY0ODcsImV4cCI6MTYyNzk4MDA4Nywic3ViIjoiSldU5Luk54mM5rWL6K-VIiwibW9uZXkiOiIzNTAwIiwiY29tcGFueSI6Ium7kemprOiuree7g-iQpSIsImFkcmVzcyI6IuS4reWNl-a1tyJ9.vfeIfmsAD6eqRlTgbgWeZ6bIgD-gggFhK5rtkzt51bg";
        Claims claims =  Jwts.parser()
                .setSigningKey("itcast")//密钥（盐）
                .parseClaimsJws(token)//要解析的令牌对象
                .getBody();//获取解析后的数据

        System.out.println(claims);
    }
}
