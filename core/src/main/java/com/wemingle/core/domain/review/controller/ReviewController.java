package com.wemingle.core.domain.review.controller;

import com.wemingle.core.domain.review.dto.ReviewDto;
import com.wemingle.core.domain.review.entity.TeamReview;
import com.wemingle.core.domain.review.service.TeamReviewService;
import com.wemingle.core.global.responseform.ResponseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController("/review")
public class ReviewController {
    private final TeamReviewService teamReviewService;

    @GetMapping("/my")
    ResponseEntity<ResponseHandler<Object>> getMyReviews(@RequestParam Long nextIdx,
                                                         @AuthenticationPrincipal UserDetails userDetails) {
        List<ReviewDto> myReviews = teamReviewService.getMyReviews(userDetails.getUsername(), nextIdx);
        return ResponseEntity.ok(ResponseHandler.builder().responseMessage("My reviews retrieval successfully").responseData(myReviews).build());
    }
    @GetMapping("/group")
    ResponseEntity<ResponseHandler<Object>> getGroupReviews(@RequestParam Long nextIdx,
                                                            @RequestParam Long groupId) {
        List<ReviewDto> myReviews = teamReviewService.getGroupReviews(groupId, nextIdx);
        return ResponseEntity.ok(ResponseHandler.builder().responseMessage("My reviews retrieval successfully").responseData(myReviews).build());
    }
}
