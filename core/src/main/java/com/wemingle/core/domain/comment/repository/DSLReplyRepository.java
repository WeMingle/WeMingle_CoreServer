package com.wemingle.core.domain.comment.repository;

import com.wemingle.core.domain.comment.entity.Reply;

import java.util.List;

public interface DSLReplyRepository {
    List<Reply> findRepliesByNextIdx(Long nextIdx, Long commentPk);
}
