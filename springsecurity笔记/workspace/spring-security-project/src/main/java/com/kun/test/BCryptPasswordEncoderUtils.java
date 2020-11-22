package com.kun.test;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @description: <p></p>
 * @author: hounaikun
 * @create: 2020-10-14 13:43
 **/
public class BCryptPasswordEncoderUtils {
    public static void main(String[] args) {
        //每次加密的后的密文都不一样
        //加盐加密，动态加盐
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String password = bCryptPasswordEncoder.encode("123456");
        System.out.println(password);
    }
}
