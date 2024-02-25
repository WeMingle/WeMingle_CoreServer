package com.wemingle.core.domain.member.repository;

import com.wemingle.core.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByMemberId(String memberId);

    @Override
    Optional<Member> findById(Long uuid);
    Optional<Member> findByRefreshToken(String refreshToken);
    Optional<Member> findByEmail(String email);
}
