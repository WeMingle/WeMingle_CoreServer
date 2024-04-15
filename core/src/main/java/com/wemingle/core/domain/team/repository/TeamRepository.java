package com.wemingle.core.domain.team.repository;

import com.wemingle.core.domain.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long>, DSLTeamRepository{
    List<Team> findByTeamOwner_MemberId(String memberId);
    @Query(value = "select * from Team order by RAND() limit 4",nativeQuery = true)
    List<Team> getRandomTeam();
    boolean existsByPkLessThan(Long teamPk);
}
