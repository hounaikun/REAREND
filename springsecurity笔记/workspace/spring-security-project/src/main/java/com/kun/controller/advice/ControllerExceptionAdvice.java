package com.kun.controller.advice;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @description: <p></p>
 * @author: hounaikun
 * @create: 2020-10-25 00:45
 **/
@ControllerAdvice
public class ControllerExceptionAdvice {
    //只有出现AccessDeniedException异常才调转403.jsp页面
    @ExceptionHandler(AccessDeniedException.class)
    public String exceptionAdvice(){
        return "forward:/403.jsp";
    }
}
