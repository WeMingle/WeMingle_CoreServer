package com.wemingle.core.domain.post.repository;

import com.wemingle.core.domain.post.entity.TeamPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamPostRepository extends JpaRepository<TeamPost, Long>, DSLTeamPostRepository {
}
