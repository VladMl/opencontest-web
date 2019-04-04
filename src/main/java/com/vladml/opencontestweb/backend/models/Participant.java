package com.vladml.opencontestweb.backend.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Participant {

    private Long contestId;

    private String callsign;

    private String locator;

    private int countryId;

    private String bands;

    private String comments;

    private int band50;

    private int band145;

    private int band435;

    private int band57;

    private int band10;

    private int band24;

    private int band47;


}
