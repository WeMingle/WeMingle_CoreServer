package com.wemingle.core.domain.member.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wemingle.core.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.wemingle.core.domain.member.entity.QMember.member;

@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DSLMemberRepositoryImpl implements DSLMemberRepository{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Member> getMemberByNickname(Long nextIdx,
                                            String nickname) {
            return jpaQueryFactory.selectFrom(member)
                    .where(
                            nextIdxLt(nextIdx),
                            member.nickname.contains(nickname)
                    )
                    .limit(3)
                    .orderBy(member.pk.desc())
                    .fetch();
        }

        private BooleanExpression nextIdxLt(Long nextIdx) {
            return nextIdx == null ? null : member.pk.loe(nextIdx);
        }
}
