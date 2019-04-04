package com.vladml.opencontestweb.frontend.controllers;

import com.vladml.opencontestweb.backend.models.Contest;
import com.vladml.opencontestweb.backend.services.ContestService;
import com.vladml.opencontestweb.backend.services.LangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@CrossOrigin

public class EdiController {

    @Autowired
    private LangService langService;

    @Autowired
    private HttpServletRequest request;


    @GetMapping("/edi")
    public ModelAndView edi(Map<String, Object> model, @CookieValue(value = "lang",
            defaultValue = "") String lang) {
        model = langService.translateModel(model, LangService.getLang(request,lang), "edi_map_upload");
        return new ModelAndView("edi_map_upload", model);
    }

    @GetMapping("/edi/map/{filename}")
    public ModelAndView ediMap(Map<String, Object> model, @CookieValue(value = "lang",
            defaultValue = "") String lang, @PathVariable(name="filename") String filename) {
        model = langService.translateModel(model, LangService.getLang(request,lang), "edi_map");
        return new ModelAndView("edi_map", model);
    }

}
