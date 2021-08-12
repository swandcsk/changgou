package com.changgou.token;

import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.Jwts;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.ws.rs.core.MultivaluedMap;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;

/*****
 * @Author: www.itheima
 * @Date: 2019/7/7 13:42
 * @Description: com.changgou.token
 *      创建JWT令牌，使用私钥加密
 ****/
public class CreateJwtTest {

    /***
     * 创建令牌测试
     */
    @Test
    public void testCreateToken(){
        //证书文件路径
        String key_location="changgou.jks";
        //秘钥库密码
        String key_password="changgou";
        //秘钥密码
        String keypwd = "changgou";
        //秘钥别名
        String alias = "changgou";

        //访问证书路径 读取jks的文件
        ClassPathResource resource = new ClassPathResource(key_location);

        //创建秘钥工厂
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(resource,key_password.toCharArray());

        //读取秘钥对(公钥、私钥)
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair(alias,keypwd.toCharArray());

        //获取私钥
        RSAPrivateKey rsaPrivate = (RSAPrivateKey) keyPair.getPrivate();

        //自定义Payload
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("id", "1");
        tokenMap.put("name", "itheima");
        tokenMap.put("roles", "ROLE_VIP,ROLE_USER");
        tokenMap.put("authorities",new String[]{"admin","oauth"});

        //生成Jwt令牌
        Jwt jwt = JwtHelper.encode(JSON.toJSONString(tokenMap), new RsaSigner(rsaPrivate));


        //取出令牌
        String encoded = jwt.getEncoded();
        System.out.println(encoded);
    }

//    @Test
//    public void test() {
//        MultiValueMap<String,Object> body = new LinkedMultiValueMap();
//        body.add("robod",1);
//        body.add("robod",2);
//        System.out.println(body.toSingleValueMap());
//        Map hashMap = new HashMap();
//        hashMap.
//    }

    /**
     * 解析令牌
     *
     */
    @Test
    public void testParseToken(){
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6IlJPTEVfVklQLFJPTEVfVVNFUiIsIm5hbWUiOiJpdGhlaW1hIiwiaWQiOiIxIn0.oNiqGqqsoTARQyzSPM3rdxw6Etp7l8DZ7d4AYS61zJ724a0a7XhI0fWuY3rknutJfDmUsOD7cnA1eVfvnCjuj42WRZGNBdYuNfdN68hBwCFiW8LMN7ke3OgH7FqyLwxFvCsUPC6LeXDasai3kuetCef1iXfIj8JlUfAsGWgqfpDnzvQn8ggeta84M6geR0bbt9cZPq-XqRhTXRM5PoMWnO9juwzFdm2xkmyO7FilO-pCkXKS4YvgMS7bgi20YfpNcadJge3IgWnOnOs48OiAZqeI2BcXO45t0Jwvja2z24WSIAlkTlOlYHQarFOf6PUvWWxORLAi4ytlsws590E_dQ";
        String publickey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAoZT6+HO1EAT8mTHJlK6LO3vM8lweAcMhuLFFZ8zIQfjocTYlPM6QCsrJ9bB5sGQFw6tdGktMuOyV8oPjeFA8iR2cUpFbTE+g5L0TCz04A8oMP43eRBHV1YeEv/dCTAWt9enJ8LNaIRzZafhs0a9p8tKy8oguM2lHt9UEuzqTF7hZDUEP5yJOPkBgk7LZVaXR6OYsxz5ckT76Z8lotcq3ZDo7vIAumLqLScx6JafCuFvvl6VoPfAp/7NwM3cQAwpyc+lvUhL/nM/uDukhcKM/JKdy/sXkCPl3hqDogtGHTN+Ui4biVkV2IIDj428jxV1aTVywWaCQ+hW0ihXEMGzjrQIDAQAB-----END PUBLIC KEY-----";
        Jwt jwt = JwtHelper.decodeAndVerify(token,
                new RsaVerifier(publickey));
        String claims = jwt.getClaims();
        System.out.println(claims);
    }

}
