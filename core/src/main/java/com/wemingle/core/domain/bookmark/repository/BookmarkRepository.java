package com.wemingle.core.domain.bookmark.repository;

import com.wemingle.core.domain.bookmark.entity.BookmarkedMatchingPost;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.post.entity.MatchingPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<BookmarkedMatchingPost,Long>,DSLBookmarkRepository {
    @Query("select bm from BookmarkedMatchingPost bm " +
            "where bm.matchingPost in :matchingPostList and bm.member.memberId = :memberId")
    List<BookmarkedMatchingPost> findBookmarkedByMatchingPosts(@Param("matchingPostList") List<MatchingPost> matchingPostList, @Param("memberId") String memberId);
    List<BookmarkedMatchingPost> findByMatchingPost(MatchingPost matchingPosts);
    boolean existsByMatchingPostAndMember(MatchingPost matchingPost, Member member);
    Optional<BookmarkedMatchingPost> findByMatchingPost_Pk(Long matchingPostPk);
}
