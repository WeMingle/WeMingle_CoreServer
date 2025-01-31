package com.wemingle.core.domain.matching.repository;

import com.wemingle.core.domain.post.entity.matchingstatus.MatchingStatus;

public interface DSLMatchingRepository {
    Integer findCompleteMatchingCnt(String memberId, MatchingStatus matchingStatus);
    Integer findScheduledMatchingCnt(String memberId, MatchingStatus matchingStatus);
}
