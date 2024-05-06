package com.wemingle.core.domain.matching.controller;

import com.wemingle.core.domain.matching.controller.requesttype.RequestType;
import com.wemingle.core.domain.matching.dto.MatchingRequestDto;
import com.wemingle.core.domain.matching.service.MatchingRequestService;
import com.wemingle.core.domain.post.entity.recruitertype.RecruiterType;
import com.wemingle.core.global.responseform.ResponseHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/match/request")
@RequiredArgsConstructor
public class MatchingRequestController {
    private final MatchingRequestService matchingRequestService;

    @Value("${wemingle.ip}")
    private String serverIp;

    private static final String MATCHING_POST_DETAIL_PATH = "/post/match/";
    @GetMapping("/history")
    public ResponseEntity<ResponseHandler<List<MatchingRequestDto.ResponseMatchingRequestHistory>>> getMatchingRequestHistories(@RequestParam(required = false) Long nextIdx,
                                                                                                                                @RequestParam(required = false) RequestType requestType,
                                                                                                                                @RequestParam(required = false) RecruiterType recruiterType,
                                                                                                                                @RequestParam boolean excludeCompleteMatchesFilter,
                                                                                                                                @AuthenticationPrincipal UserDetails userDetails) {
        String memberId = userDetails.getUsername();
        List<MatchingRequestDto.ResponseMatchingRequestHistory> responseData = matchingRequestService.getMatchingRequestHistories(nextIdx, requestType, recruiterType, excludeCompleteMatchesFilter, memberId);

        return ResponseEntity.ok(ResponseHandler.<List<MatchingRequestDto.ResponseMatchingRequestHistory>>builder()
                .responseMessage("Matching Request Histories retrieval successfully")
                .responseData(responseData)
                .build());
    }

    @GetMapping("/team/{matchingPostPk}")
    public ResponseEntity<ResponseHandler<MatchingRequestDto.ResponsePendingRequestsByTeam>> getPendingMatchingRequestByTeam(@PathVariable Long matchingPostPk){

        MatchingRequestDto.ResponsePendingRequestsByTeam responseData = matchingRequestService.getPendingRequestsByTeam(matchingPostPk);
        return ResponseEntity.ok(ResponseHandler.<MatchingRequestDto.ResponsePendingRequestsByTeam>builder()
                .responseMessage("Pending Matching Request By Team retrieval successfully")
                .responseData(responseData)
                .build());
    }

    @GetMapping("/individual/{matchingPostPk}")
    public ResponseEntity<ResponseHandler<MatchingRequestDto.ResponsePendingRequestsByIndividual>> getPendingMatchingRequestByIndividual(@PathVariable Long matchingPostPk){

        MatchingRequestDto.ResponsePendingRequestsByIndividual responseData = matchingRequestService.getPendingRequestsByIndividual(matchingPostPk);
        return ResponseEntity.ok(ResponseHandler.<MatchingRequestDto.ResponsePendingRequestsByIndividual>builder()
                .responseMessage("Pending Matching Request By Individual retrieval successfully")
                .responseData(responseData)
                .build());
    }

    @PatchMapping
    public ResponseEntity<Object> approveMatchingRequests(@RequestBody MatchingRequestDto.MatchingRequestApprove matchingRequestApprove){
        matchingRequestService.approveMatchingRequests(matchingRequestApprove);

        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<ResponseHandler<String>> saveMatchingRequest(@RequestBody @Valid MatchingRequestDto.RequestMatchingRequestSave requestSaveDto,
                                                                       @AuthenticationPrincipal UserDetails userDetails){
        matchingRequestService.saveMatchingRequest(requestSaveDto, userDetails.getUsername());

        String createdUrl = serverIp + MATCHING_POST_DETAIL_PATH + "/" + requestSaveDto.getMatchingPostPk();
        return ResponseEntity.created(URI.create(createdUrl)).build();
    }
}
