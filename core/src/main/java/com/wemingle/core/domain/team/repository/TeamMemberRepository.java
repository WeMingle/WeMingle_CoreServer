package com.wemingle.core.domain.team.repository;

import com.wemingle.core.domain.team.entity.Team;
import com.wemingle.core.domain.team.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    Optional<TeamMember> findByTeamAndMember_MemberId(Team team, String memberId);
    @Query("select tm from TeamMember tm where tm.member.memberId = :memberId")
    List<TeamMember> findTeamsAsLeaderOrMember(@Param("memberId") String memberId);
}
