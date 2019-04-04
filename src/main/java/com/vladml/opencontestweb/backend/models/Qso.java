package com.vladml.opencontestweb.backend.models;

import com.vladml.opencontestweb.backend.services.LangService;
import lombok.Builder;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Data
@Builder
public class Qso {

    private int index;

    private String callsign;

    private String locator;

    private String callsignOriginal;

    private String callsignWkd;

    private String locatorWkd;

    private int countryId;

    private Date date;

    private String dateFormatted;

    private String mode;

    private String bandEdi;

    private String rstSent;

    private String rstRcvd;

    private String exchSent;

    private String exchRcvd;

    private int distance;

    private int score;

    private String errorDescription;

    private int errorReasonId;

    private int contestId;

    private boolean error;

    private boolean bandDelimiter;

    private boolean crossLink;

    private String crossCheckDetails;

    public String dateFormatted() {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.format(date);
    }

    public boolean getError() {
        return getErrorReasonId() == 0 ? false : true;
    }

    public boolean getCrossLink() {
        return getErrorReasonId() != 100 ? true : false;
    }


    public String errorDescription() {
        if (errorDescription.isEmpty())
            return errorDescription;
        if ((errorReasonId == 3) || (errorReasonId == 6) || (errorReasonId == 7))
           return  errorDescription + crossCheckDetails.split("\\|")[1];
        else if ((errorReasonId == 103) || (errorReasonId == 106) || (errorReasonId == 107))
           return  errorDescription + crossCheckDetails.split("\\|")[0];
        else return  errorDescription;
    }


    public Qso translate(String lang) {
        if ( !errorDescription.isEmpty() )
            errorDescription = LangService.getTranslation(errorDescription, lang);
        return this;
    }


}
