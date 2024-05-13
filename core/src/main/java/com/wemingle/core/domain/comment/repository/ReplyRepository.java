package com.wemingle.core.domain.comment.repository;

import com.wemingle.core.domain.comment.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
}
