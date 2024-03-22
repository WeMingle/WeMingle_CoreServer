package com.wemingle.core.domain.matching.repository;

import com.wemingle.core.domain.matching.entity.Matching;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchingRepository extends JpaRepository<Matching, Long> {
}
