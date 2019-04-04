package com.vladml.opencontestweb.backend.services;

import com.vladml.opencontestweb.backend.models.Band;
import com.vladml.opencontestweb.backend.models.Contest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ContestService {

    @Autowired
    JdbcTemplate jdbcTemplate;


    private static final RowMapper<Contest> contestMapper = (rs, i) ->
            Contest.builder()
                    .contestName(rs.getString("contest_name"))
                    .date(rs.getDate("date"))
                    .dateStart(rs.getDate("date_start"))
                    .dateFinish(rs.getDate("date_finish"))
                    .contestTitleTemplate(rs.getString("contest_title_template"))
                    .stateId(rs.getLong("state_id"))
                    .stateTitleTemplate(rs.getString("state_title_template"))
                    .contestSchedulesId(rs.getLong("contest_schedules_id"))
                    .resultOverall(rs.getInt("result_overall") == 1)
                    .resultUkraine(rs.getInt("result_ukraine") == 1)
                    .build();

    private static final RowMapper<Band> contestBandsMapper = (rs, i) ->
            Band.builder()
            .id(rs.getLong("band_id"))
            .bandEdi(rs.getString("band_edi"))
            .build();



    public List<Contest> getAllContests() {
        return jdbcTemplate.query(
                "SELECT * FROM v_contest_list",
                contestMapper);
    }

    public List<Contest> getAllContestsCurryenYear() {
        return jdbcTemplate.query(
                "SELECT * FROM v_contest_list where YEAR(date) = YEAR(now())",
                contestMapper);
    }


    public Optional<Contest> getContestById(Long id) {
        List<Contest> contestList = jdbcTemplate.query(
                "SELECT * FROM v_contest_list WHERE contest_schedules_id = ?",
                contestMapper,
                id);
        if (contestList.size() > 1) throw new RuntimeException("Too many results from findOrderById.");
        return contestList.size() == 1 ? Optional.of(contestList.get(0)) : Optional.empty();
    }


    public static Contest translateContest(Contest contest, String lang) {
        contest.setContestTitleTemplate(LangService.getTranslation(contest.getContestTitleTemplate(), lang));
        contest.setStateTitleTemplate(LangService.getTranslation(contest.getStateTitleTemplate(), lang));
        return contest;
    }

    public Map<String, Object> getContestModel(Long id, String lang) {
        Contest contest = getContestById(id).orElseThrow(
                () -> new IllegalArgumentException("Invalid ID")
        );
        contest = ContestService.translateContest(contest, lang);
        Map<String, Object> model = new HashMap<>();
        model.put("ContestTitle",contest.getContestTitle());
        model.put("ContestState", contest.getStateTitleTemplate());
        model.put("contestSchedulesId", id);
        model.put("resultOverall", contest.isResultOverall());
        model.put("resultUkraine", contest.isResultUkraine());
        return model;
    }


    public List<Band> getContestBands(Long id) {
        return jdbcTemplate.query(
                "SELECT * FROM v_contest_bands WHERE contest_schedules_id = ?",
                contestBandsMapper,
                id);
    }




}
