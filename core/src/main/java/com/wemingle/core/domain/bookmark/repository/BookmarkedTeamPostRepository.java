package com.wemingle.core.domain.bookmark.repository;

import com.wemingle.core.domain.bookmark.entity.BookmarkedTeamPost;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.post.entity.TeamPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookmarkedTeamPostRepository extends JpaRepository<BookmarkedTeamPost, Long> {
    @Query("select bt.teamPost from BookmarkedTeamPost bt " +
            "where bt.teamPost in :teamPosts and bt.member.memberId = :memberId")
    List<TeamPost> findBookmarkedByTeamPost(@Param("teamPosts") List<TeamPost> teamPosts,
                                            @Param("memberId") String memberId);
    @Query("select bt.teamPost from BookmarkedTeamPost bt " +
            "where bt.member = :member")
    List<TeamPost> findTeamPostByMember(@Param("member")Member member);
    @Query("select bt.teamPost from BookmarkedTeamPost bt where bt.teamPost.team.pk = :teamId and bt.member.memberId = :memberId and bt.pk <= :nextIdx")
    List<TeamPost> findBookmarkedTeamPost(@Param("teamId") Long teamId, @Param("memberId") String memberId, @Param("nextIdx")Long nextIdx);
    boolean existsByTeamPostAndMember(TeamPost teamPost, Member member);
    Optional<BookmarkedTeamPost> findByTeamPost_PkAndMember_MemberId(Long teamPostPk, String memberId);
    List<BookmarkedTeamPost> findByTeamPostIn(List<TeamPost> teamPosts);
}
