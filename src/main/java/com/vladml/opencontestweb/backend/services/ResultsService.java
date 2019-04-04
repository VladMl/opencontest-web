package com.vladml.opencontestweb.backend.services;

import com.vladml.opencontestweb.backend.models.Station;
import com.vladml.opencontestweb.backend.models.Qso;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ResultsService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    private LangService langService;


    private static final RowMapper<Station> stationClaimedMapper = (rs, i) ->
            Station.builder()
            .id(rs.getLong("id"))
            .callsign(rs.getString("callsign"))
            .countryId(rs.getLong("country_id"))
            .locator(rs.getString("locator"))
            .claimedQso(rs.getInt("claimed_qso"))
            .claimedScore(rs.getInt("claimed_score"))
            .category(rs.getString("category"))
            .build();

    private static final RowMapper<Station> stationFinalMapper = (rs, i) ->
            Station.builder()
                    .id(rs.getLong("id"))
                    .callsign(rs.getString("callsign"))
                    .callsignOriginal(rs.getString("callsign_original"))
                    .countryId(rs.getLong("country_id"))
                    .locator(rs.getString("locator"))
                    .bandEdi(rs.getString("band_edi"))
                    .claimedQso(rs.getInt("claimed_qso"))
                    .claimedScore(rs.getInt("claimed_score"))
                    .category(rs.getString("category"))
                    .confirmedQso(rs.getInt("confirmed_qso"))
                    .confirmedScore(rs.getInt("confirmed_score"))
                    .errTime(rs.getInt("errors_time"))
                    .errExch(rs.getInt("errors_numbers"))
                    .errRst(rs.getInt("errors_rst"))
                    .errLoc(rs.getInt("errors_locator"))
                    .errNil(rs.getInt("errors_qso_not_found"))
                    .errNoReport(rs.getInt("errors_not_reports"))
                    .percentConfirmation(rs.getInt("percent_confirmation"))
                    .id(rs.getLong("id"))
                    .build();


    private static final RowMapper<Station> stationMissingMapper = (rs, i) ->
            Station.builder()
                    .callsign(rs.getString("callsign"))
                    .countryId(rs.getLong("country_id"))
                    .build();


    private static final RowMapper<Qso> QsoMapper = (rs, i) ->
            Qso.builder()
                    .callsign(rs.getString("callsign"))
                    .date(rs.getTimestamp("date"))
                    .countryId(rs.getInt("country_id"))
                    .locator(rs.getString("locator"))
                    .callsignWkd(rs.getString("callsign_wkd"))
                    .locatorWkd(rs.getString("locator_wkd"))
                    .mode(rs.getString("mode"))
                    .bandEdi(rs.getString("band_edi"))
                    .rstSent(rs.getString("rst_sent"))
                    .rstRcvd(rs.getString("rst_rcvd"))
                    .exchSent(rs.getString("exch_sent"))
                    .exchRcvd(rs.getString("exch_rcvd"))
                    .distance(rs.getInt("distance"))
                    .score(rs.getInt("score"))
                    .errorDescription(rs.getString("error_description"))
                    .errorReasonId(rs.getInt("error_reason_id"))
                    .contestId(rs.getInt("contest_id"))
                    .crossCheckDetails(rs.getString("crosscheck_details"))
                    .build();



    private static Map<String, Object> groupByCategoryBand(List<Station> stationList, boolean groupByBand, String lang) {
        Map<String, Object> resultMap = new LinkedHashMap<>();
        String category, bandEdi;
        category = bandEdi = "";

        int index = 0 ;
        List<Station> stationListcategory = null;

        for (Station s : stationList) {
            if (!s.getCategory().equals(category)) {
                if (stationListcategory != null && stationListcategory.size()>0)
                    resultMap.put(LangService.getTranslation(category, lang), stationListcategory);
                category = s.getCategory();
                stationListcategory = new ArrayList<>();
                index = 0;
            }
            resultMap.put(LangService.getTranslation(category, lang), stationListcategory);

            // Group by band
            if (groupByBand && !s.getBandEdi().equals(bandEdi)) {
                index = 0;
                bandEdi = s.getBandEdi();
                stationListcategory.add(Station.builder()
                        .bandEdi(bandEdi)
                        .bandDelimiter(true)
                        .build());
            }

            s.setIndex(++index);
            stationListcategory.add(s);
        }
        return resultMap;
    }

    private static List<Qso> groupQsoByBand(List<Qso> report) {
        String bandEdi = "";
        int index = 0;

        List<Qso> resultList = new ArrayList<>();
        for (Qso qso : report) {
            if ( !qso.getBandEdi().equals(bandEdi) ) {
                index = 0;
                qso.setBandDelimiter(true);
                bandEdi = qso.getBandEdi();
            }
            qso.setIndex(++index);
            resultList.add(qso);
        }
        return resultList;
    }



    public Map<String, Object> getClaimedResults(Long id, String lang) {
        List<Station> stationList =
        jdbcTemplate.query(
                "SELECT s.* FROM (SELECT @id:=? id) parm , v_claimed_results s ORDER BY sort_order, claimed_score DESC;",
                stationClaimedMapper,
                id);
        return groupByCategoryBand(stationList, false, lang);
    }


    public Map<String, Object> getFinalResults(Long id, int viewId, String lang) {
        List<Station> stationList =
                jdbcTemplate.query(
                        "SELECT s.* FROM (SELECT @id:=? id) parm , v_final_results s WHERE s.view_id=? ORDER BY s.sort_order, s.band_mhz ASC, s.confirmed_score DESC;",
                        stationFinalMapper,
                        id, viewId);

        return groupByCategoryBand(stationList, true, lang);
    }


    public List<Station> getMissingStations(Long id) {
        List<Station> stationList =
                jdbcTemplate.query(
                        "SELECT * FROM (SELECT lq.log_qso_call callsign, COUNT(*) qso_count , lq.log_qso_country as country_id FROM log_qso lq, stations s WHERE s.station_id =lq.station_id AND s.contest_schedules_id = ? AND s.category_id NOT IN (3,4) AND lq.log_qso_call NOT IN (SELECT ss.station_call FROM stations ss WHERE ss.contest_schedules_id = ? ) GROUP BY lq.log_qso_call, lq.log_qso_country ORDER BY lq.log_qso_country, 2 desc ) qry",
                        stationMissingMapper,
                        id, id);
        return stationList;
    }


    public List<Qso> getStationReport(Long id, String callsign) {

        List<Qso> report =
                jdbcTemplate.query(
                        "SELECT s.* FROM (SELECT @id:=? id, @callsign:=? callsign) parm , v_station_report s;",
                        QsoMapper,
                        id, callsign);

        return groupQsoByBand(report);
    }



}
