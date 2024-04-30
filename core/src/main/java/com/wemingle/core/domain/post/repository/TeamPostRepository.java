package com.wemingle.core.domain.post.repository;

import com.wemingle.core.domain.post.entity.TeamPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamPostRepository extends JpaRepository<TeamPost, Long>, DSLTeamPostRepository {
    List<TeamPost> findByTeam_Pk(Long teamPk);
}
