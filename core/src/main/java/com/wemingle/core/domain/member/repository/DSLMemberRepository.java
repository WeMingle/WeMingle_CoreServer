package com.wemingle.core.domain.member.repository;

import com.wemingle.core.domain.member.entity.Member;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DSLMemberRepository {
    List<Member> getMemberByNickname(Long nextIdx,
                                     String nickname,
                                     Pageable pageable);
}
