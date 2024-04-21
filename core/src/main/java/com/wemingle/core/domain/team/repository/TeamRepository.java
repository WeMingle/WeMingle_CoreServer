package com.wemingle.core.domain.team.repository;

import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.team.entity.Team;
import com.wemingle.core.domain.team.entity.teamtype.TeamType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long>, DSLTeamRepository{
    List<Team> findByTeamOwner_MemberId(String memberId);
    boolean existsByPkLessThanAndTeamNameContainsAndTeamType(Long teamPk, String teamName, TeamType teamType);
    @Query("select t from Team t " +
            "where t.teamOwner in :members and t.teamType = 'TEAM' " +
            "order by t.pk desc")
    List<Team> findByTeamOwnerIn(@Param("members")List<Member> members, Pageable pageable);
}
