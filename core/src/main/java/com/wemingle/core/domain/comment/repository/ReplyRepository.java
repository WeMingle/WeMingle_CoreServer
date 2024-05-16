package com.wemingle.core.domain.comment.repository;

import com.wemingle.core.domain.comment.entity.Reply;
import com.wemingle.core.domain.post.entity.TeamPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    @Query("select r.comment.teamPost from Reply r inner join r.comment inner join r.comment.teamPost " +
            "where r.pk = :replyPk")
    Optional<TeamPost> findTeamPostByReplyPk(@Param("replyPk") Long replyPk);
    @Query("select r from Reply r join fetch r.writer where r.pk <= :nextIdx and r.comment.pk = :commentPk " +
            "order by r.pk desc limit 20")
    List<Reply> findRepliesByNextIdx(@Param("nextIdx") Long nextIdx, @Param("commentPk")Long commentPk);
    boolean existsByPkLessThanAndCommentPk(Long replyPk, Long commentPk);
}
