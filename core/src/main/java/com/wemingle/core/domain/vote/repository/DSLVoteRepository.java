package com.wemingle.core.domain.vote.repository;

import com.wemingle.core.domain.post.entity.TeamPost;
import com.wemingle.core.domain.vote.entity.TeamPostVote;

import java.util.List;

public interface DSLVoteRepository {
    List<TeamPostVote> getExpiredVotes(Long nextIdx, List<TeamPost> teamPosts);
}
