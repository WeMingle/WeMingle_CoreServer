package com.wemingle.core.domain.comment.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wemingle.core.domain.comment.entity.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.wemingle.core.domain.comment.entity.QComment.comment;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DSLCommentRepositoryImpl implements DSLCommentRepository{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Comment> findCommentByNextIdx(Long nextIdx, Long teamPostPk) {
        return jpaQueryFactory.select(comment)
                .from(comment)
                .join(comment.writer).fetchJoin()
                .where(
                        nextIdxLoe(nextIdx),
                        teamPostPKEq(teamPostPk)
                )
                .orderBy(comment.pk.desc())
                .limit(51)
                .fetch();
    }

    private BooleanExpression nextIdxLoe(Long nextIdx) {
        return nextIdx == null ? null : comment.pk.loe(nextIdx);
    }

    private BooleanExpression teamPostPKEq(Long teamPostPk){
        return comment.teamPost.pk.eq(teamPostPk);
    }
}
