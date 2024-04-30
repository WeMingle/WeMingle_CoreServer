package com.wemingle.core.domain.vote.repository;

import com.wemingle.core.domain.vote.entity.TeamPostVote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamPostVoteRepository extends JpaRepository<TeamPostVote, Long>, DSLVoteRepository{
}
