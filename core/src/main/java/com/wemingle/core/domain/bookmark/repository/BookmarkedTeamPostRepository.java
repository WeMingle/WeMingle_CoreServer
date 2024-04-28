package com.wemingle.core.domain.bookmark.repository;

import com.wemingle.core.domain.bookmark.entity.BookmarkedTeamPost;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.post.entity.TeamPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookmarkedTeamPostRepository extends JpaRepository<BookmarkedTeamPost, Long> {
    @Query("select bt.teamPost from BookmarkedTeamPost bt " +
            "where bt.teamPost in :teamPosts and bt.member.memberId = :memberId")
    List<TeamPost> findBookmarkedByTeamPost(@Param("teamPosts") List<TeamPost> teamPosts,
                                            @Param("memberId") String memberId);
    @Query("select bt.teamPost from BookmarkedTeamPost bt " +
            "where bt.member = :member")
    List<TeamPost> findTeamPostByMember(@Param("member")Member member);
}
