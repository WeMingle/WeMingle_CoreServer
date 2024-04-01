package com.wemingle.core.domain.matching.repository;

public interface DSLMatchingRequestRepository {
    Integer findReceivedMatchingCnt(String memberId);
}
