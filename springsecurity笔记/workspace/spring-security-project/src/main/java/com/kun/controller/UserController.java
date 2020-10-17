package com.kun.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @description: <p></p>
 * @author: hounaikun
 * @create: 2020-10-14 10:17
 **/
@Controller
@RequestMapping("/user")
public class UserController {
    @RequestMapping("/main")
    public String login(){
        return "main";
    }
}
