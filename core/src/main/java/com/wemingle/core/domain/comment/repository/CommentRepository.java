package com.wemingle.core.domain.comment.repository;

import com.wemingle.core.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("select c from Comment c join fetch c.teamPost where c.pk = :commentPk")
    Optional<Comment> findByIdFetchPost(@Param("commentPk")Long commentPk);
}
