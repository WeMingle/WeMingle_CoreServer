package com.wemingle.core.domain.bookmark.repository;

import com.wemingle.core.domain.bookmark.entity.BookmarkedMatchingPost;
import com.wemingle.core.domain.post.entity.MatchingPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookmarkRepository extends JpaRepository<BookmarkedMatchingPost,Long> {
    @Query("select bm from BookmarkedMatchingPost bm " +
            "where bm.matchingPost in :matchingPostList and bm.member.memberId = :memberId")
    List<BookmarkedMatchingPost> findBookmarkedByMatchingPosts(@Param("matchingPostList") List<MatchingPost> matchingPostList, @Param("memberId") String memberId);
}
