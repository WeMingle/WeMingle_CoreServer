package com.wemingle.core.domain.team.repository;

import com.wemingle.core.domain.team.entity.Team;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DSLTeamRepository {
    List<Team> getTeamByTeamName(Long nextIdx,
                                 String teamName,
                                 Pageable pageable);
}
