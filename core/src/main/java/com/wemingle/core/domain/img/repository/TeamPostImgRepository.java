package com.wemingle.core.domain.img.repository;

import com.wemingle.core.domain.img.entity.TeamPostImg;
import com.wemingle.core.domain.post.entity.TeamPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamPostImgRepository extends JpaRepository<TeamPostImg, Long> {
    List<TeamPostImg> findByTeamPostIn(List<TeamPost> teamPosts);
}
