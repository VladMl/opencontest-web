package com.vladml.opencontestweb.frontend.controllers;


import com.vladml.opencontestweb.backend.models.Band;
import com.vladml.opencontestweb.backend.models.Participant;
import com.vladml.opencontestweb.backend.services.ContestService;
import com.vladml.opencontestweb.backend.services.LangService;
import com.vladml.opencontestweb.backend.services.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
public class ParticipantsController {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private LangService langService;

    @Autowired
    private ContestService contestService;

    @Autowired
    private ParticipantService participantService;


    @GetMapping("/participants/{id}")
    public ModelAndView participants(Map<String, Object> model, @CookieValue(value = "lang",
            defaultValue = "") String lang, @PathVariable Long id) {

        List<Band> bands = contestService.getContestBands(id);


        model = langService.translateModel(model, LangService.getLang(request, lang), "participants");
        model.putAll(contestService.getContestModel(id, LangService.getLang(request, lang)));
        model.put("listBands", bands);
        model.put("participantList", participantService.getAllParticipants(id));

        return new ModelAndView("participants", model);
    }

    @PostMapping("/participants/{id}")
    public String participantAdd(@PathVariable Long id) {

        if (!request.getParameter("callsign").equals(""))
            participantService.addParticipant(
                    Participant.builder()
                            .contestId(id)
                            .callsign(request.getParameter("callsign").toUpperCase())
                            .locator(request.getParameter("locator").toUpperCase())
                            .comments(request.getParameter("comments"))
                            .band50((request.getParameter("band10") != null) ? 1 : 0)
                            .band145((request.getParameter("band11") != null) ? 1 : 0)
                            .band435((request.getParameter("band13") != null) ? 1 : 0)
                            .band57((request.getParameter("band18") != null) ? 1 : 0)
                            .band10((request.getParameter("band19") != null) ? 1 : 0)
                            .band24((request.getParameter("band20") != null) ? 1 : 0)
                            .band47((request.getParameter("band21") != null) ? 1 : 0)
                            .build());
        return "redirect:" + request.getRequestURI();
    }


}
