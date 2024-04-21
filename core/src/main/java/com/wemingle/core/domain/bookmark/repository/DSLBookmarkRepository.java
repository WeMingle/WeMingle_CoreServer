package com.wemingle.core.domain.bookmark.repository;

import com.wemingle.core.domain.post.entity.MatchingPost;
import com.wemingle.core.domain.post.entity.recruitertype.RecruiterType;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface DSLBookmarkRepository {
    List<MatchingPost> findMyBookmarkedList(Long nextIdx, String memberId, LocalDate currentDate, RecruiterType recruiterType, Pageable pageable);
}
