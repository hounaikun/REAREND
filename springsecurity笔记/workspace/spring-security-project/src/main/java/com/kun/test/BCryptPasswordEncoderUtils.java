package com.kun.test;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @description: <p></p>
 * @author: hounaikun
 * @create: 2020-10-14 13:43
 **/
public class BCryptPasswordEncoderUtils {
    public static void main(String[] args) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String password = bCryptPasswordEncoder.encode("user");
        System.out.println(password);
    }
}
