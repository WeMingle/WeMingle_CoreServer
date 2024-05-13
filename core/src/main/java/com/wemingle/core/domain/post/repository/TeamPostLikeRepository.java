package com.wemingle.core.domain.post.repository;

import com.wemingle.core.domain.post.entity.TeamPost;
import com.wemingle.core.domain.post.entity.TeamPostLike;
import com.wemingle.core.domain.team.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamPostLikeRepository extends JpaRepository<TeamPostLike, Long> {
    Optional<TeamPostLike> findByTeamPostAndTeamMember(TeamPost teamPost, TeamMember teamMember);
}
