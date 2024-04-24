package com.wemingle.core.domain.bookmark.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wemingle.core.domain.post.entity.MatchingPost;
import com.wemingle.core.domain.post.entity.recruitertype.RecruiterType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static com.wemingle.core.domain.bookmark.entity.QBookmarkedMatchingPost.bookmarkedMatchingPost;
import static com.wemingle.core.domain.post.entity.QMatchingPost.matchingPost;

@Repository
@RequiredArgsConstructor
public class DSLBookmarkRepositoryImpl implements DSLBookmarkRepository{
    private final JPAQueryFactory jpaQueryFactory;
    @Override
    public List<MatchingPost> findMyBookmarkedList(Long nextIdx, String memberId, LocalDate currentDate, RecruiterType recruiterType, Pageable pageable) {
        return jpaQueryFactory.select(matchingPost)
                .from(bookmarkedMatchingPost)
                .innerJoin(bookmarkedMatchingPost.matchingPost)
                .where(nextIdx(nextIdx),
                        bookmarkedMatchingPost.member.memberId.eq(memberId),
                        isExpired(currentDate),
                        sortOption(recruiterType))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(bookmarkedMatchingPost.createdTime.desc())
                .fetch();
    }

    BooleanExpression nextIdx(Long nextIdx) {
        return Objects.isNull(nextIdx) ? null : bookmarkedMatchingPost.pk.lt(nextIdx);
    }

    BooleanExpression isExpired(LocalDate currentDate) {
        return Objects.isNull(currentDate) ? null : bookmarkedMatchingPost.matchingPost.expiryDate.after(currentDate);
    }

    BooleanExpression sortOption(RecruiterType recruiterType) {
        return Objects.isNull(recruiterType) ? null : bookmarkedMatchingPost.matchingPost.recruiterType.eq(recruiterType);
    }
}
