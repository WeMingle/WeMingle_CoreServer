package com.wemingle.core.domain.bookmark.controller;

import com.wemingle.core.domain.bookmark.dto.GroupBookmarkDto;
import com.wemingle.core.domain.bookmark.dto.RequestMyBookMarkListDto;
import com.wemingle.core.domain.bookmark.service.BookmarkService;
import com.wemingle.core.domain.post.dto.MatchingPostDto;
import com.wemingle.core.global.responseform.ResponseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;
    @PostMapping("/match/posts/{postId}")
    ResponseEntity<ResponseHandler<Object>> createMatchingPostBookmark(@PathVariable long postId, @AuthenticationPrincipal UserDetails userDetails) {
        bookmarkService.saveMatchingPostBookmark(postId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/match/posts/{postId}")
    ResponseEntity<ResponseHandler<Object>> deleteMatchingPostBookmark(@PathVariable long postId,
                                                                       @AuthenticationPrincipal UserDetails userDetails) {
        bookmarkService.deleteMatchingPostBookmark(postId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/teams/posts/{postId}")
    ResponseEntity<ResponseHandler<Object>> createTeamPostBookmark(@PathVariable long postId,
                                                                   @AuthenticationPrincipal UserDetails userDetails) {
        bookmarkService.saveTeamPostBookmark(postId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/teams/posts/{postId}")
    ResponseEntity<ResponseHandler<Object>> deleteTeamPostBookmark(@PathVariable long postId,
                                                                   @AuthenticationPrincipal UserDetails userDetails) {
        bookmarkService.deleteTeamPostBookmark(postId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/match/posts")
    ResponseEntity<ResponseHandler<Object>> getMyBookMarkList(@ModelAttribute RequestMyBookMarkListDto requestMyBookMarkListDto, @AuthenticationPrincipal UserDetails userDetails) {
        List<MatchingPostDto.ResponseMyBookmarkDto> myBookmarkedList = bookmarkService.getMyBookmarkedList(requestMyBookMarkListDto, userDetails.getUsername());

        return ResponseEntity.ok().body(ResponseHandler.builder()
                .responseMessage("Bookmark list retrieval successfully")
                .responseData(myBookmarkedList)
                .build());
    }

    @GetMapping("/teams/posts")
    ResponseEntity<ResponseHandler<Object>> getGroupBookmarkList(@RequestParam(required = false) Long nextIdx, @RequestParam(required = false) Long teamId, @AuthenticationPrincipal UserDetails userDetails) {
        List<GroupBookmarkDto> groupBookmarkedList = bookmarkService.getGroupBookmarkedList(nextIdx, userDetails.getUsername(), teamId);
        return ResponseEntity.ok().body(ResponseHandler.builder()
                .responseMessage("Group bookmark list retrieval successfully")
                .responseData(groupBookmarkedList).build());
    }
}
