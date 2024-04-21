package com.wemingle.core.domain.member.repository;

import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.member.entity.MemberAbility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberAbilityRepository extends JpaRepository<MemberAbility,Long> {
    List<MemberAbility> findMemberAbilitiesByMember(Member member);
}
