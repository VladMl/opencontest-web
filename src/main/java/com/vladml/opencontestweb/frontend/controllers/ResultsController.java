package com.vladml.opencontestweb.frontend.controllers;


import com.vladml.opencontestweb.backend.models.Qso;
import com.vladml.opencontestweb.backend.models.Station;
import com.vladml.opencontestweb.backend.services.ContestService;
import com.vladml.opencontestweb.backend.services.LangService;
import com.vladml.opencontestweb.backend.services.ResultsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
public class ResultsController {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private LangService langService;

    @Autowired
    ContestService contestService;

    @Autowired
    ResultsService resultsService;


    @GetMapping("/results/claimed/{id}")
    public ModelAndView claimedResults(Map<String, Object> model, @CookieValue(value = "lang",
            defaultValue = "") String lang, @PathVariable Long id) {
        model = langService.translateModel(model, LangService.getLang(request,lang), "claimed_results");
        model.putAll(contestService.getContestModel(id,LangService.getLang(request,lang)));
        Map<String, Object> stationList = resultsService.getClaimedResults(id, LangService.getLang(request,lang));
        model.put("stationList", stationList.entrySet());
        model.put("claimedResults", true);
        model.put("resultsClaimedPage", true);
        return new ModelAndView("claimed_results", model);
    }


    @GetMapping("/results/claimed/missing/{id}")
    public ModelAndView claimedMissingReports(Map<String, Object> model, @CookieValue(value = "lang",
            defaultValue = "") String lang, @PathVariable Long id) {
        model = langService.translateModel(model, LangService.getLang(request,lang), "claimed_results");
        model.putAll(contestService.getContestModel(id,LangService.getLang(request,lang)));
        List<Station> stationList = resultsService.getMissingStations(id);
        model.put("StationList", stationList);
        model.put("missingReports", true);
        model.put("missingReportsPage", true);
        return new ModelAndView("claimed_results", model);
    }

    @GetMapping("/results/final/all/{id}")
    public ModelAndView finalAllResults(Map<String, Object> model, @CookieValue(value = "lang",
            defaultValue = "") String lang, @PathVariable Long id) {
        model = langService.translateModel(model, LangService.getLang(request,lang), "final_results");
        model.putAll(contestService.getContestModel(id,LangService.getLang(request,lang)));
        Map<String, Object> stationList = resultsService.getFinalResults(id, 0, LangService.getLang(request,lang));
        model.put("StationList", stationList.entrySet());
        model.put("contestId", id);
        model.put("finalResults", true);
        model.put("resultsOverallPage", true);
        return new ModelAndView("final_results", model);
    }

    @GetMapping("/results/final/ukraine/{id}")
    public ModelAndView finalUkraineResults(Map<String, Object> model, @CookieValue(value = "lang",
            defaultValue = "") String lang, @PathVariable Long id) {
        model = langService.translateModel(model, LangService.getLang(request,lang), "final_results");
        model.putAll(contestService.getContestModel(id,LangService.getLang(request,lang)));
        Map<String, Object> stationList = resultsService.getFinalResults(id, 1, LangService.getLang(request,lang));
        model.put("stationList", stationList.entrySet());
        model.put("contestId", id);
        model.put("finalResults", true);
        model.put("resultsUkrainePage", true);
        return new ModelAndView("final_results", model);
    }


    @GetMapping("/results/final/missing/{id}")
    public ModelAndView finalMissingReports(Map<String, Object> model, @CookieValue(value = "lang",
            defaultValue = "") String lang, @PathVariable Long id) {
        model = langService.translateModel(model, LangService.getLang(request,lang), "final_results");
        model.putAll(contestService.getContestModel(id,LangService.getLang(request,lang)));
        List<Station> stationList = resultsService.getMissingStations(id);
        model.put("stationList", stationList);
        model.put("missingReports", true);
        model.put("missingReportsPage", true);
        return new ModelAndView("final_results", model);
    }


    @GetMapping("/results/{id}/station/{callsign}")
    public ModelAndView stationResults(Map<String, Object> model, @CookieValue(value = "lang",
            defaultValue = "") String lang, @PathVariable Long id, @PathVariable String callsign) {
        model = langService.translateModel(model, LangService.getLang(request,lang), "report");
        model.putAll(contestService.getContestModel(id,LangService.getLang(request,lang)));
        List<Qso> stationReport = resultsService.getStationReport(id, callsign);
        stationReport.stream().forEach((cl) -> cl.translate(LangService.getLang(request,lang)));
        model.put("stationReport", stationReport);
        if (stationReport.size()>0) {
            model.put("callsign", stationReport.get(0).getCallsign());
            model.put("locatior", stationReport.get(0).getLocator());
        } else {
            model.put("callsign", "");
            model.put("locator", "");
        }
        return new ModelAndView("report", model);
    }


}
