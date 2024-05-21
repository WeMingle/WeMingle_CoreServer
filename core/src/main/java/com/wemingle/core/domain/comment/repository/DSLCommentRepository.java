package com.wemingle.core.domain.comment.repository;

import com.wemingle.core.domain.comment.entity.Comment;

import java.util.List;

public interface DSLCommentRepository {
    List<Comment> findCommentByNextIdx(Long nextIdx, Long teamPostPk);
}
