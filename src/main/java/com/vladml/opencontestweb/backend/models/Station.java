package com.vladml.opencontestweb.backend.models;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Station {

    private Long id;

    private int index;

    private String callsign;

    private String callsignOriginal;

    private Long countryId;

    private String locator;

    private String category;

    private String bandEdi;

    private int claimedQso;

    private int claimedScore;

    private int confirmedQso;

    private int confirmedScore;

    private int errTime;

    private int errExch;

    private int errRst;

    private int errLoc;

    private int errNil;

    private int errNoReport;

    private int percentConfirmation;

    private boolean bandDelimiter;




}
