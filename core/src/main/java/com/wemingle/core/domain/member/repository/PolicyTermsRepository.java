package com.wemingle.core.domain.member.repository;

import com.wemingle.core.domain.member.entity.PolicyTerms;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PolicyTermsRepository extends JpaRepository<PolicyTerms, Long> {
}
