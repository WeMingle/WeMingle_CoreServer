package com.wemingle.core.domain.bookmark.service;

import com.wemingle.core.domain.bookmark.entity.BookmarkedMatchingPost;
import com.wemingle.core.domain.bookmark.repository.BookmarkRepository;
import com.wemingle.core.domain.img.service.S3ImgService;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.member.service.MemberService;
import com.wemingle.core.domain.post.dto.MatchingPostDto;
import com.wemingle.core.domain.post.entity.MatchingPost;
import com.wemingle.core.domain.post.entity.MatchingPostArea;
import com.wemingle.core.domain.post.entity.recruitertype.RecruiterType;
import com.wemingle.core.domain.post.service.MatchingPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final MemberService memberService;
    private final MatchingPostService matchingPostService;
    private final S3ImgService s3ImgService;

    public void saveBookmark(long postId,String memberId) {
        Member member = memberService.findByMemberId(memberId);
        MatchingPost post = matchingPostService.getMatchingPostByPostId(postId);
        BookmarkedMatchingPost bookmarkedMatchingPost = BookmarkedMatchingPost.builder()
                .matchingPost(post)
                .member(member)
                .build();
        bookmarkRepository.save(bookmarkedMatchingPost);
    }

    public List<BookmarkedMatchingPost> getBookmarkedByMatchingPosts(List<MatchingPost> matchingPostList, String memberId) {
        return bookmarkRepository.findBookmarkedByMatchingPosts(matchingPostList, memberId);
    }

    public List<MatchingPostDto.ResponseMyBookmarkDto> getMyBookmarkedList(Long nextIdx, Boolean excludeExpired, RecruiterType recruiterType, String memberId) {
        ArrayList<MatchingPostDto.ResponseMyBookmarkDto> matchingPostDtoList = new ArrayList<>();
        bookmarkRepository.findMyBookmarkedList(nextIdx, memberId, excludeExpired ? null : LocalDate.now(), recruiterType, PageRequest.of(0, 30))
                .forEach(matchingPost -> matchingPostDtoList.add(
                        MatchingPostDto.ResponseMyBookmarkDto.builder()
                                .pk(matchingPost.getPk())
                                .matchingDate(matchingPost.getMatchingDate())
                                .isBookmarked(true)
                                .ability(matchingPost.getAbility())
                                .recruiterType(matchingPost.getRecruiterType())
                                .areaList(matchingPost.getAreaList().stream().map(MatchingPostArea::getAreaName).toList())
                                .profilePicUrl(
                                        matchingPost.getRecruiterType().equals(RecruiterType.TEAM) ?
                                                s3ImgService.getGroupProfilePicUrl(matchingPost.getWriter().getProfileImg()) :
                                                s3ImgService.getMemberProfilePicUrl(matchingPost.getWriter().getProfileImg())
                                )
                                .writer(matchingPost.getWriter().getNickname())
                                .matchingCnt(matchingPost.getTeam().getCompletedMatchingCnt())
                                .isLocationConsensusPossible(matchingPost.isLocationConsensusPossible())
                                .contents(matchingPost.getContent())
                                .build()
                ));
        return matchingPostDtoList;
    }
}
