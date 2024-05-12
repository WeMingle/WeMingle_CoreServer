package com.wemingle.core.domain.team.repository;

import com.wemingle.core.domain.category.sports.entity.sportstype.SportsType;
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
    @Query("select tm.team from TeamMember tm where tm.member = :member and tm.teamRole != 'PARTICIPANT' " +
            "and (tm.team.teamType = 'INDIVIDUAL' or (tm.team.teamType = 'TEAM' and tm.team.sportsCategory = :sportType))")
    List<Team> findTeamsWithAvailableWrite(@Param("sportType") SportsType sportsType, @Param("member") Member memberId);
    @Query("select tm from TeamMember tm where tm.team in :teams and (tm.teamRole = 'OWNER' or tm.teamRole = 'MANAGER')")
    List<TeamMember> findWithManagerOrHigher(@Param("teams") List<Team> teams);
    @Query("select tm from TeamMember tm where tm.team.pk = :teamPk and tm.member.memberId != :memberId")
    List<TeamMember> findWithTeamWithoutMe(@Param("teamPk") Long teamPk, @Param("memberId") String memberId);
    Optional<TeamMember> findByTeam_PkAndNickname(Long teamPk, String nickname);
    @Query("select tm.team from TeamMember tm where tm.member = :member and tm.teamRole != 'PARTICIPANT' and tm.team.teamType = :teamType " +
            "and (tm.team.teamType = 'INDIVIDUAL' or (tm.team.teamType = 'TEAM' and tm.team.sportsCategory = :sportType))")
    List<Team> findTeamsWithAvailableRequest(@Param("member") Member memberId, @Param("sportType") SportsType sportsType, @Param("teamType") TeamType teamType);
    @Query("select tm.member from TeamMember tm where tm.pk in :teamMembersPk")
    List<Member> findMemberByTeamMemberIdIn(@Param("teamMembersPk")List<Long> teamMemberPks);
    List<TeamMember> findByTeamAndMemberIn(Team team, List<Member> members);
}
