package com.wemingle.core.domain.post.repository;

import com.wemingle.core.domain.post.entity.MatchingPost;
import com.wemingle.core.domain.team.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MatchingPostRepository extends JpaRepository<MatchingPost,Long>, DSLMatchingPostRepository {
    Optional<MatchingPost> findByWriter(TeamMember writer);
}
