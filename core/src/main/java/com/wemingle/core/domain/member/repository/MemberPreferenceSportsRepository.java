package com.wemingle.core.domain.member.repository;

import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.member.entity.MemberPreferenceSports;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberPreferenceSportsRepository extends JpaRepository<MemberPreferenceSports, Long> {
    MemberPreferenceSports findByMember(Member member);
}
