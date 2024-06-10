package com.wemingle.core.domain.vote.repository;

import com.wemingle.core.domain.post.entity.TeamPost;
import com.wemingle.core.domain.vote.entity.TeamPostVote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamPostVoteRepository extends JpaRepository<TeamPostVote, Long>, DSLVoteRepository{
    List<TeamPostVote> findByTeamPostIn(List<TeamPost> teamPosts);
}
