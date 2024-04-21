package com.wemingle.core.domain.member.repository;

import com.wemingle.core.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long>, DSLMemberRepository {
    Optional<Member> findByMemberId(String memberId);
    Optional<Member> findByRefreshToken(String refreshToken);
    Optional<Member> findByNickname(String nickname);
    List<Member> findByMemberIdIn(List<String> memberIdList);
    boolean existsByPkLessThanAndNicknameContains(Long memberPk, String nickname);
}
