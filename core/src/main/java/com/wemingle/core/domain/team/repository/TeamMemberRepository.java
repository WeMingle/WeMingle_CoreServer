package com.wemingle.core.domain.team.repository;

import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.team.entity.Team;
import com.wemingle.core.domain.team.entity.TeamMember;
import com.wemingle.core.domain.team.entity.teamtype.TeamType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    Optional<TeamMember> findByTeamAndMember_MemberId(Team team, String memberId);
    Optional<TeamMember> findByTeamAndMember(Team team, Member member);
    @Query("select tm from TeamMember tm where tm.member.memberId = :memberId")
    List<TeamMember> findTeamsAsLeaderOrMember(@Param("memberId") String memberId);
    boolean existsByMember(Member member);
    @Query("select tm.team from TeamMember tm where tm.member.memberId = :memberId and tm.team.teamType = 'TEAM'")
    List<Team> findMyTeams(@Param("memberId") String memberId);
    @Query("select tm.team from TeamMember tm where tm.member = :member and tm.teamRole != 'PARTICIPANT'")
    List<Team> findTeamsWithAvailableWrite(@Param("member") Member memberId);
    @Query("select tm from TeamMember tm where tm.team in :teams and (tm.teamRole = 'OWNER' or tm.teamRole = 'MANAGER')")
    List<TeamMember> findWithManagerOrHigher(@Param("teams") List<Team> teams);
    @Query("select tm from TeamMember tm where tm.team.pk = :teamPk and tm.member.memberId != :memberId")
    List<TeamMember> findWithTeamWithoutMe(@Param("teamPk") Long teamPk, @Param("memberId") String memberId);
    Optional<TeamMember> findByTeam_PkAndNickname(Long teamPk, String nickname);
    @Query("select tm.team from TeamMember tm where tm.member = :member and tm.teamRole != 'PARTICIPANT' and tm.team.teamType = :teamType")
    List<Team> findTeamsWithAvailableRequest(@Param("member") Member memberId, @Param("teamType") TeamType teamType);
}
