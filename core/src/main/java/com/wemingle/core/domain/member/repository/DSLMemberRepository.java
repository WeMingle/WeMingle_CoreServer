package com.wemingle.core.domain.member.repository;

import com.wemingle.core.domain.member.entity.Member;

import java.util.List;

public interface DSLMemberRepository {
    List<Member> getMemberByNickname(Long nextIdx,
                                     String nickname);
}
