package com.wemingle.core.domain.comment.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wemingle.core.domain.comment.entity.Reply;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.wemingle.core.domain.comment.entity.QReply.reply;


@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DSLReplyRepositoryImpl implements DSLReplyRepository{
    private final JPAQueryFactory jpaQueryFactory;
    @Override
    public List<Reply> findRepliesByNextIdx(Long nextIdx, Long commentPk) {
        return jpaQueryFactory.select(reply)
                .from(reply)
                .join(reply.writer).fetchJoin()
                .where(
                        nextIdxLoe(nextIdx),
                        commentPkEq(commentPk)
                )
                .orderBy(reply.pk.desc())
                .limit(11)
                .fetch();
    }

    private BooleanExpression nextIdxLoe(Long nextIdx) {
        return nextIdx == null ? null : reply.pk.loe(nextIdx);
    }

    private BooleanExpression commentPkEq(Long commentPk){
        return reply.comment.pk.eq(commentPk);
    }
}
