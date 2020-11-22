package com.kun.controller.advice;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @description: <p></p>
 * @author: hounaikun
 * @create: 2020-10-25 00:36
 **/
public class HandlerControllerException implements HandlerExceptionResolver {
    /**
     *
     * @param request
     * @param response
     * @param handler 出现异常的对象，就是哪个对象出现异常了
     * @param ex 出现的异常信息
     * @return ModelAndView
     */
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ModelAndView modelAndView = new ModelAndView();
        if(ex instanceof AccessDeniedException){
            modelAndView.setViewName("redirect:/403.jsp");
        }
        return modelAndView;
    }
}
