package com.wemingle.core.domain.member.repository;

import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.member.entity.MemberPreferenceSports;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberPreferenceSportsRepository extends JpaRepository<MemberPreferenceSports, Long> {
    List<MemberPreferenceSports> findByMember(Member member);
}
