package com.vladml.opencontestweb.backend.services;

import com.vladml.opencontestweb.backend.models.Band;
import com.vladml.opencontestweb.backend.models.Participant;
import com.vladml.opencontestweb.backend.util.HamUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ParticipantService {

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    HamUtils hamUtils;


    private static final RowMapper<Participant> participantsMapper = (rs, i) ->
            Participant.builder()
            .callsign(rs.getString("callsign"))
            .countryId(rs.getInt("code_adif"))
            .locator(rs.getString("locator"))
            .bands(rs.getString("bands"))
            .comments(rs.getString("comments"))
            .build();


    public List<Participant> getAllParticipants(Long contestId) {
        Map namedParameters = new HashMap();
        namedParameters.put("contestId", contestId);
        return jdbcTemplate.query(
                "SELECT * FROM v_participants WHERE contest_schedules_id = :contestId ",
                namedParameters,
                participantsMapper );

    }


    public void addParticipant(Participant participant) {

        Map namedParameters = new HashMap();
        namedParameters.put("contestId", participant.getContestId());
        namedParameters.put("callsign", participant.getCallsign());
        namedParameters.put("locator", participant.getLocator());
        namedParameters.put("comments", participant.getComments());
        namedParameters.put("countryId", hamUtils.getCountryId(participant.getCallsign()));
        namedParameters.put("band50", participant.getBand50());
        namedParameters.put("band145", participant.getBand145());
        namedParameters.put("band435", participant.getBand435());
        namedParameters.put("band57", participant.getBand57());
        namedParameters.put("band10", participant.getBand10());
        namedParameters.put("band24", participant.getBand24());
        namedParameters.put("band47", participant.getBand47());


        jdbcTemplate.update(
                "INSERT INTO participants(contest_schedules_id, callsign, locator, comments, code_adif, band_50, band_144, band_432, band_5_7, band_10, band_24)\n" +
                        " VALUES\n" +
                        "    (:contestId, " +
                        "     :callsign, " +
                        "     :locator, " +
                        "     :comments, " +
                        "     :countryId," +
                        "     :band50, " +
                        "     :band145," +
                        "     :band435," +
                        "     :band57," +
                        "     :band10," +
                        "     :band24 )\n" +
                        " ON DUPLICATE KEY UPDATE\n" +
                        "    locator = :locator,\n" +
                        "    comments = :comments, \n" +
                        "    code_adif = :countryId, \n" +
                        "    band_50 = :band50, \n" +
                        "    band_144 = :band145, \n" +
                        "    band_432 = :band435, \n" +
                        "    band_5_7 = :band57, \n" +
                        "    band_10 = :band10, \n" +
                        "    band_24 = :band24;",
                namedParameters

        );

    }


}
