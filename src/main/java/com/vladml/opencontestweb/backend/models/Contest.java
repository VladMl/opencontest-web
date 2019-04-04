package com.vladml.opencontestweb.backend.models;

import com.vladml.opencontestweb.backend.services.LangService;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;


@Data
@Builder
public class Contest {


    private Long contestSchedulesId;

    private Date date;

    private Date dateStart;

    private Date dateFinish;

    private String stateName;

    private String contestName;

    private String contestTitleTemplate;

    private String stateTitleTemplate;

    private Long stateId;


    private String formattedDate;

    private String badgeClass;

    private String resultsLink;

    private boolean stateNotStarted;

    private boolean stateReportsLoading;

    private boolean stateReportsExchanging;

    private boolean stateResultsPublished;

    private boolean resultOverall;

    private boolean resultUkraine;

    private String contestTitle;

    private String reportsZipName;

    public String getFormattedDate() {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        return format.format(date);
    }

    public String getBadgeClass() {
        if (stateId == 1)
            return "badge-grey";
        else if (stateId == 2)
            return "badge-danger";
        else if (stateId == 3)
            return "badge-secondary";
        else if (stateId == 9)
            return "badge-warning";
        else if (stateId == 10)
            return "badge-success";
        else
            return "badge-info";
    }



    public String getResultsLink() {
        if (stateId == 1)
            return "<a href=\"participants/" + contestSchedulesId + "\">[#Listofparticipants]</a>";
        return "";
    }

    public boolean getStateNotStarted() {
        return stateId == 1;
    }

    public boolean getStateReportsLoading() {
        return stateId == 2;
    }

    public boolean getStateReportsExchanging() {
        return stateId > 2 && stateId < 9;
    }

    public boolean getStateResultsPublished() {
        return stateId > 8;
    }

    public String getContestTitle() {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        return getContestTitleTemplate() + " [" + format.format(getDateStart()) + " - " + format.format(getDateFinish()) + "]";
    }

    public String getReportsZipName() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return "UR_"+format.format(getDateStart())+".zip";
    }

}
