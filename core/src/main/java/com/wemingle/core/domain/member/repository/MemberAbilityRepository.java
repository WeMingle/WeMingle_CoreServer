package com.wemingle.core.domain.member.repository;

import com.wemingle.core.domain.category.sports.entity.sportstype.SportsType;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.member.entity.MemberAbility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberAbilityRepository extends JpaRepository<MemberAbility,Long> {
    List<MemberAbility> findMemberAbilitiesByMember(Member member);
    List<MemberAbility> findByMemberInAndSportsType(List<Member> member, SportsType sportsType);
    @Query("select ma.ability from MemberAbility ma where ma.member = :member and ma.sportsType = :sportsType")
    Optional<String> findAbilityByMemberAndSport(@Param("member")Member member, @Param("sportsType") SportsType sportsType);
}
