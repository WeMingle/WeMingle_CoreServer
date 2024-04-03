package com.wemingle.core.domain.matching.service;

import com.wemingle.core.domain.matching.repository.MatchingRepository;
import com.wemingle.core.domain.matching.repository.MatchingRequestRepository;
import com.wemingle.core.domain.post.entity.matchingstatus.MatchingStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

@Service
@RequiredArgsConstructor
public class MatchingService {
    private final MatchingRequestRepository matchingRequestRepository;
    private final MatchingRepository matchingRepository;

    public LinkedHashMap<String, Integer> getMatchingSummaryInfo(String memberId) {
        Integer completeMatchingCnt = matchingRepository.findCompleteMatchingCnt(memberId, MatchingStatus.COMPLETE);
        Integer scheduledMatchingCnt = matchingRepository.findScheduledMatchingCnt(memberId, MatchingStatus.COMPLETE);
        Integer requestedMatchingCnt = matchingRequestRepository.findRequestedMatchingCnt(memberId, MatchingStatus.PENDING,MatchingStatus.PENDING);
        Integer receivedMatchingCnt = matchingRequestRepository.findReceivedMatchingCnt(memberId);
        LinkedHashMap<String, Integer> responseNode = new LinkedHashMap<>();
        responseNode.put("completeMatchingCnt", completeMatchingCnt);
        responseNode.put("scheduledMatchingCnt", scheduledMatchingCnt);
        responseNode.put("requestedMatchingCnt", requestedMatchingCnt);
        responseNode.put("receivedMatchingCnt", receivedMatchingCnt);
        return responseNode;
    }
}
