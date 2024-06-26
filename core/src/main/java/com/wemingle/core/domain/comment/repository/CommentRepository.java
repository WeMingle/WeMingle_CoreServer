package com.wemingle.core.domain.comment.repository;

import com.wemingle.core.domain.comment.entity.Comment;
import com.wemingle.core.domain.post.entity.TeamPost;
import com.wemingle.core.domain.team.entity.Team;
import com.wemingle.core.domain.team.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long>, DSLCommentRepository {
    @Query("select c from Comment c join fetch c.teamPost where c.pk = :commentPk")
    Optional<Comment> findByIdFetchPost(@Param("commentPk")Long commentPk);
    @Query("select c.teamPost.team from Comment c where c.pk = :commentPk")
    Optional<Team> findTeam(@Param("commentPk")Long commentPk);
    List<Comment> findByTeamPostIn(List<TeamPost> teamPosts);
    List<Comment> findByWriter(TeamMember writer);
}
