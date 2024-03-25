package com.wemingle.core.domain.bookmark.service;

import com.wemingle.core.domain.bookmark.entity.BookmarkedMatchingPost;
import com.wemingle.core.domain.bookmark.repository.BookmarkRepository;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.member.service.MemberService;
import com.wemingle.core.domain.post.entity.MatchingPost;
import com.wemingle.core.domain.post.service.MatchingPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final MemberService memberService;
    private final MatchingPostService matchingPostService;

    public void saveBookmark(long postId,String memberId) {
        Member member = memberService.findByMemberId(memberId);
        MatchingPost post = matchingPostService.getMatchingPostByPostId(postId);
        BookmarkedMatchingPost bookmarkedMatchingPost = BookmarkedMatchingPost.builder()
                .matchingPost(post)
                .member(member)
                .build();
        bookmarkRepository.save(bookmarkedMatchingPost);
    }
}
