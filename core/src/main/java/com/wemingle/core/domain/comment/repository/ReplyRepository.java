package com.wemingle.core.domain.comment.repository;

import com.wemingle.core.domain.comment.entity.Reply;
import com.wemingle.core.domain.post.entity.TeamPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReplyRepository extends JpaRepository<Reply, Long>, DSLReplyRepository{
    @Query("select r.comment.teamPost from Reply r inner join r.comment inner join r.comment.teamPost " +
            "where r.pk = :replyPk")
    Optional<TeamPost> findTeamPostByReplyPk(@Param("replyPk") Long replyPk);
    @Query(value = "select * from (" +
            "   select *, rank() over (partition by comment order by pk desc) as rn " +
            "   from reply) as ranking " +
            "where ranking.rn <= 11 and ranking.comment in :comments",nativeQuery = true)
    List<Reply> findRankRepliesByComments(@Param("comments") List<Long> comments);
}
