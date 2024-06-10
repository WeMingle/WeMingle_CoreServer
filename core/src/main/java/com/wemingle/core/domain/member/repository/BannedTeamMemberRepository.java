package com.wemingle.core.domain.member.repository;

import com.wemingle.core.domain.member.entity.BannedTeamMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BannedTeamMemberRepository extends JpaRepository<BannedTeamMember, Long> {
    Optional<BannedTeamMember> findByTeam_PkAndBannedMember_MemberId(Long teamId, String memberId);
}
