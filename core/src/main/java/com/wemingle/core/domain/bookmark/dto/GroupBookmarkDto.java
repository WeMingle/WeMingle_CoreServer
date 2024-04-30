package com.wemingle.core.domain.bookmark.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;

@Getter
@Setter
public class GroupBookmarkDto {
    private Long pk;
    private String title;
    private String content;
    private LocalDateTime writeTime;
    private String writer;
    private List<String> picUrlList;
    private Integer likeCnt;
    private Integer replyCnt;
    private boolean isBookmarked;
    private GroupVote groupVote;

    @Builder
    public GroupBookmarkDto(Long pk, String title, String content, LocalDateTime writeTime, String writer, List<String> picUrlList, Integer likeCnt, Integer replyCnt, boolean isBookmarked, GroupVote groupVote) {
        this.pk = pk;
        this.title = title;
        this.content = content;
        this.writeTime = writeTime;
        this.writer = writer;
        this.picUrlList = picUrlList;
        this.likeCnt = likeCnt;
        this.replyCnt = replyCnt;
        this.isBookmarked = isBookmarked;
        this.groupVote = groupVote;
    }

    public static class GroupVote{
        private Long pk;
        private String title;
        private LocalDateTime expiryTime;
        private LinkedHashMap<String,Integer> voteResults;

        @Builder
        public GroupVote(Long pk, String title, LocalDateTime expiryTime, LinkedHashMap<String, Integer> voteResults) {
            this.pk = pk;
            this.title = title;
            this.expiryTime = expiryTime;
            this.voteResults = voteResults;
        }
    }
}
