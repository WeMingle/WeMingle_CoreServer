package com.wemingle.core.domain.vote.repository;

import com.wemingle.core.domain.vote.entity.VoteOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteOptionRepository extends JpaRepository<VoteOption, Long> {
}
