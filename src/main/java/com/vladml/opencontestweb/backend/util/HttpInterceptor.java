package com.vladml.opencontestweb.backend.util;

import com.vladml.opencontestweb.backend.services.LangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class HttpInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    LangService langService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        LangService.setLang(request, response);
        return true;
    }
}
