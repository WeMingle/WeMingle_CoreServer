package com.wemingle.core.domain.team.repository;

import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.team.entity.Team;
import com.wemingle.core.domain.team.entity.teamtype.TeamType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeamRepository extends JpaRepository<Team, Long>, DSLTeamRepository{
    List<Team> findByTeamOwner_MemberId(String memberId);
    boolean existsByPkLessThanAndTeamNameContainsAndTeamType(Long teamPk, String teamName, TeamType teamType);
    @Query("select t from Team t " +
            "where t.teamOwner in :members and t.teamType = 'TEAM' " +
            "order by t.pk desc")
    List<Team> findByTeamOwnerIn(@Param("members")List<Member> members, Pageable pageable);

    @Query("select t.profileImgId from Team t " +
            "where t.teamOwner.memberId = :ownerId and t.teamName = :teamName")
    Optional<UUID> findTeamProfileImgId(@Param("ownerId") String memberId, @Param("teamName") String teamName);

    @Query("select t from Team t where t.teamName = :ownerId and t.teamOwner.memberId = :ownerId")
    Optional<Team> findTemporaryTeam(@Param("ownerId") String ownerId);
    @Query("select t from Team t order by t.completedMatchingCnt desc limit 15")
    List<Team> find15PopularTeamOrIndividual();
}
