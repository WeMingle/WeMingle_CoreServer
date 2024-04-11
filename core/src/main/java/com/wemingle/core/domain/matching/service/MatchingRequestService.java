package com.wemingle.core.domain.matching.service;

import com.wemingle.core.domain.matching.controller.requesttype.RequestType;
import com.wemingle.core.domain.matching.dto.MatchingRequestDto;
import com.wemingle.core.domain.matching.dto.requesttitlestatus.RequestTitleStatus;
import com.wemingle.core.domain.matching.entity.MatchingRequest;
import com.wemingle.core.domain.matching.repository.MatchingRepository;
import com.wemingle.core.domain.matching.vo.TitleInfo;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.member.repository.MemberRepository;
import com.wemingle.core.domain.post.entity.MatchingPost;
import com.wemingle.core.domain.post.entity.recruitertype.RecruiterType;
import com.wemingle.core.domain.post.repository.MatchingPostRepository;
import com.wemingle.core.global.exceptionmessage.ExceptionMessage;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.wemingle.core.global.exceptionmessage.ExceptionMessage.MEMBER_NOT_FOUNT;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MatchingRequestService {
    private final MatchingRepository matchingRepository;
    private final MatchingPostRepository matchingPostRepository;
    private final MemberRepository memberRepository;

    private static final String IS_OWNER_SENT_SUFFIX = "에 매칭 신청을 보냈습니다.";
    private static final String IS_PARTICIPANT_TITLE_PREFIX = "내가 속한 ";
    private static final String IS_PARTICIPANT_SENT_SUFFIX = "이 매칭 신청을 보냈습니다.";
    private static final String RECEIVE_SUFFIX = "이 매칭을 신청했습니다.";
    private static final String COMPLETE_SUFFIX = "과 매칭이 성사되었습니다.";
    private static final String CANCEL_SUFFIX = "과 매칭이 실패하였습니다.";
    private static final int PAGE_SIZE = 30;

    public List<MatchingRequestDto.ResponseMatchingRequestHistory> getMatchingRequestHistories(Long nextIdx,
                                                                                         RequestType requestType,
                                                                                         RecruiterType recruiterType,
                                                                                         boolean excludeCompleteMatchesFilter,
                                                                                         String memberId){
        Member findMember = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUNT.getExceptionMessage()));
        List<MatchingPost> myMatchingPost = matchingPostRepository.findByWriter_Member(findMember);
        PageRequest pageRequest = PageRequest.of(0, PAGE_SIZE);

        List<MatchingRequest> matchingRequestHistories = matchingRepository.findMatchingRequestHistories(nextIdx, requestType, recruiterType, excludeCompleteMatchesFilter, findMember, myMatchingPost, pageRequest);

        return matchingRequestHistories.stream().map(matchingRequest -> MatchingRequestDto.ResponseMatchingRequestHistory.builder()
                        .titleInfo(createTitleInfo(matchingRequest, myMatchingPost, findMember))
                        .content(matchingRequest.getMatchingPost().getContent())
                        .requestDate(matchingRequest.getCreatedTime().toLocalDate())
                        .matchingStatus(matchingRequest.getMatchingRequestStatus())
                        .matchingRequestPk(matchingRequest.getPk())
                        .matchingPostPk(matchingRequest.getMatchingPost().getPk())
                        .build())
                .toList();
    }

    private TitleInfo createTitleInfo(MatchingRequest matchingRequest, List<MatchingPost> myMatchingPost, Member findMember) {
        String teamName = matchingRequest.getTeam().getTeamName();
        switch (matchingRequest.getMatchingRequestStatus()){
            case COMPLETE -> {
                return new TitleInfo(teamName + COMPLETE_SUFFIX, RequestTitleStatus.COMPLETE);
            }
            case CANCEL -> {
                return new TitleInfo(teamName + CANCEL_SUFFIX, RequestTitleStatus.CANCEL);
            }
            case PENDING -> {
                if (myMatchingPost.contains(matchingRequest.getMatchingPost())){
                    return new TitleInfo(teamName + RECEIVE_SUFFIX, RequestTitleStatus.RECEIVE);
                }

                return matchingRequest.getTeam().getTeamOwner().equals(findMember)
                        ? new TitleInfo(teamName + IS_OWNER_SENT_SUFFIX, RequestTitleStatus.SENT_BY_ME)
                        : new TitleInfo(IS_PARTICIPANT_TITLE_PREFIX + teamName + IS_PARTICIPANT_SENT_SUFFIX, RequestTitleStatus.SENT_BY_OWNER);
            }
            default -> throw new RuntimeException(ExceptionMessage.INVALID_MATCHING_REQUEST_STATUS.getExceptionMessage());
        }
    }
}
