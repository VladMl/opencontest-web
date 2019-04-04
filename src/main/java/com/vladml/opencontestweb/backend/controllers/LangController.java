package com.vladml.opencontestweb.backend.controllers;

import com.vladml.opencontestweb.backend.services.LangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/lang")
public class LangController {

    @Autowired
    LangService langService;

    @GetMapping("/set/{lang}")
    public void setLang(HttpServletRequest request, HttpServletResponse response,
                        @PathVariable("lang") String lang) {
        LangService.setLang(request, response, lang);
    }

}

