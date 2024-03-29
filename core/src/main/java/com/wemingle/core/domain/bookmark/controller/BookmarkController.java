package com.wemingle.core.domain.bookmark.controller;

import com.wemingle.core.domain.bookmark.service.BookmarkService;
import com.wemingle.core.global.responseform.ResponseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/bookmark")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;
    @PostMapping
    ResponseEntity<ResponseHandler<Object>> createBookmark(@RequestBody long postId, @AuthenticationPrincipal UserDetails userDetails) {
        bookmarkService.saveBookmark(postId, userDetails.getUsername());
        return ResponseEntity.ok().body(ResponseHandler.builder().responseMessage("Bookmark Added Successfully").build());
    }
}
