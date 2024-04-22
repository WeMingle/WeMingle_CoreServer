package com.wemingle.core.domain.vote.repository;

import com.wemingle.core.domain.vote.entity.VoteResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteResultRepository extends JpaRepository<VoteResult, Long> {
}
