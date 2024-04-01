package com.wemingle.core.domain.post.repository;

import com.wemingle.core.domain.post.entity.MatchingPost;
import com.wemingle.core.domain.post.entity.MatchingPostArea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchingPostAreaRepository extends JpaRepository<MatchingPostArea,Long> {
    List<MatchingPostArea> findByMatchingPostIn(List<MatchingPost> matchingPosts);
}
