package com.wemingle.core.domain.rating.repository;

import com.wemingle.core.domain.rating.entity.TeamRating;
import com.wemingle.core.domain.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeamRatingRepository extends JpaRepository<TeamRating, Long> {
    @Query("select tr from TeamRating tr where tr.team in :teams")
    List<TeamRating> findTeamRatingInPk(@Param("teams") List<Team> team);
}
