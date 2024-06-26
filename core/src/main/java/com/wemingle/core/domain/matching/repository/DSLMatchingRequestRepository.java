package com.wemingle.core.domain.matching.repository;

import com.wemingle.core.domain.matching.controller.requesttype.RequestType;
import com.wemingle.core.domain.matching.entity.MatchingRequest;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.post.entity.MatchingPost;
import com.wemingle.core.domain.post.entity.recruitertype.RecruiterType;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DSLMatchingRequestRepository {
    Integer findReceivedMatchingCnt(String memberId);

    List<MatchingRequest> findMatchingRequestHistories(Long nextIdx,
                                                       RequestType requestType,
                                                       RecruiterType recruiterType,
                                                       boolean excludeCompleteMatchesFilter,
                                                       Member member,
                                                       List<MatchingPost> myMatchingPosts,
                                                       Pageable pageable);
}
