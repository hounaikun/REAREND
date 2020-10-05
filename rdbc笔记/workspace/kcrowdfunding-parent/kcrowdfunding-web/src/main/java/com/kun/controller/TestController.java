package com.kun.controller;

import com.kun.bean.KTest;
import com.kun.service.TestService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

/**
 * @description: <p>测试类</p>
 * @author: hounaikun
 * @create: 2020-09-30 16:38
 **/
@RequestMapping("/test")
@Controller
public class TestController {
    @Resource(name="testService")
    private TestService testService;

    @RequestMapping("/showKTestMessage")
    @ResponseBody
    public List<KTest> showUserMessage(){
        List<KTest> kTests = testService.getAllMessage();
        return kTests;
    }
    @RequestMapping("toIndexJsp")
    public String toIndexJsp(){
        return "index";
    }
}
