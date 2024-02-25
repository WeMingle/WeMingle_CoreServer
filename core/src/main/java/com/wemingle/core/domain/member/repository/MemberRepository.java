package com.wemingle.core.domain.member.repository;

import com.wemingle.core.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {
    @Override
    Optional<Member> findById(UUID uuid);

    Optional<Member> findByEmail(String email);
}
