package com.wemingle.core.domain.member.repository;

import com.wemingle.core.domain.member.entity.BannedTeamMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BannedTeamMemberRepository extends JpaRepository<BannedTeamMember, Long> {
}
