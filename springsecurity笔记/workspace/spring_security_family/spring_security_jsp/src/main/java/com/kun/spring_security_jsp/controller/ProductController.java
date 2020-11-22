package com.kun.spring_security_jsp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @description: <p></p>
 * @author: hounaikun
 * @create: 2020-10-25 15:40
 **/
@Controller
@RequestMapping("/product")
public class ProductController {

    @RequestMapping("/findAll")
    public @ResponseBody
    String findAll() {
        return "product-list";
    }
}
