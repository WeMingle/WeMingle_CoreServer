package com.wemingle.core.domain.matching.repository;

import com.wemingle.core.domain.matching.entity.TeamRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRequestRepository extends JpaRepository<TeamRequest, Long> {
}
