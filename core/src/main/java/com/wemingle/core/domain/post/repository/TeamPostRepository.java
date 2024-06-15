package com.wemingle.core.domain.post.repository;

import com.wemingle.core.domain.post.entity.TeamPost;
import com.wemingle.core.domain.team.entity.Team;
import com.wemingle.core.domain.team.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeamPostRepository extends JpaRepository<TeamPost, Long>, DSLTeamPostRepository {
    @Query("select tp from TeamPost tp where tp.team.pk = :teamId and tp.teamPostVote is not null")
    List<TeamPost> findTeamPostWithVote(@Param("teamId") Long teamId);
    @Query("select tp.team from TeamPost tp where tp.pk = :teamPostPk")
    Optional<Team> findTeam(@Param("teamPostPk")Long teamPostPk);
    List<TeamPost> findByWriter(TeamMember teamMember);
}
