package com.vladml.opencontestweb.frontend.controllers;

import com.vladml.opencontestweb.backend.models.Contest;
import com.vladml.opencontestweb.backend.services.ContestService;
import com.vladml.opencontestweb.backend.services.LangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
public class IndexController {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private LangService langService;

    @Autowired
    ContestService contestService;


    @GetMapping("/")
    public ModelAndView index(Map<String, Object> model, @CookieValue(value = "lang",
            defaultValue = "") String lang) {
        model = langService.translateModel(model, LangService.getLang(request,lang), "index");
        List<Contest> contestList = contestService.getAllContestsCurryenYear();
        contestList.stream().forEach((cl) -> ContestService.translateContest(cl,LangService.getLang(request,lang)));
        model.put("contestListItems", contestList);
        return new ModelAndView("index", model);
    }

}
