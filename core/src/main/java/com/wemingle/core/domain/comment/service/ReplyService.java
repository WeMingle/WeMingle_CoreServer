package com.wemingle.core.domain.comment.service;

import com.wemingle.core.domain.comment.dto.ReplyDto;
import com.wemingle.core.domain.comment.entity.Comment;
import com.wemingle.core.domain.comment.entity.Reply;
import com.wemingle.core.domain.comment.repository.CommentRepository;
import com.wemingle.core.domain.comment.repository.ReplyRepository;
import com.wemingle.core.domain.img.service.S3ImgService;
import com.wemingle.core.domain.post.entity.TeamPost;
import com.wemingle.core.domain.team.entity.Team;
import com.wemingle.core.domain.team.entity.TeamMember;
import com.wemingle.core.domain.team.repository.TeamMemberRepository;
import com.wemingle.core.global.exceptionmessage.ExceptionMessage;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReplyService {
    private final CommentRepository commentRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final ReplyRepository replyRepository;
    private final S3ImgService s3ImgService;

    @Value("${wemingle.ip}")
    private String serverIp;
    private static final String REPLY_RETRIEVE_PATH = "/reply";

    @Transactional
    public void saveReply(ReplyDto.RequestReplySave saveDto, String memberId){
        Comment comment = commentRepository.findByIdFetchPost(saveDto.getCommentPk())
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.COMMENT_NOT_FOUND.getExceptionMessage()));
        TeamMember requester = teamMemberRepository.findByTeamAndMember_MemberId(comment.getTeamPost().getTeam(), memberId)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.TEAM_MEMBER_NOT_FOUND.getExceptionMessage()));

        replyRepository.save(Reply.builder()
                .comment(comment)
                .writer(requester)
                .content(saveDto.getContent())
                .build());

        comment.getTeamPost().addReplyCnt();
    }

    public boolean isReplyWriter(Long replyPk, String memberId){
        Reply reply = replyRepository.findById(replyPk)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.REPLY_NOT_FOUND.getExceptionMessage()));
        TeamMember requester = teamMemberRepository.findByTeamAndMember_MemberId(reply.getWriter().getTeam(), memberId)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.TEAM_MEMBER_NOT_FOUND.getExceptionMessage()));

        return reply.isWriter(requester);
    }

    @Transactional
    public void updateReply(ReplyDto.RequestReplyUpdate updateDto){
        Reply reply = replyRepository.findById(updateDto.getReplyPk())
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.REPLY_NOT_FOUND.getExceptionMessage()));

        reply.updateContent(updateDto.getContent());
    }

    @Transactional
    public void deleteReply(ReplyDto.RequestReplyDelete deleteDto){
        Reply reply = replyRepository.findById(deleteDto.getReplyPk())
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.REPLY_NOT_FOUND.getExceptionMessage()));
        TeamPost teamPost = replyRepository.findTeamPostByReplyPk(deleteDto.getReplyPk())
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.POST_NOT_FOUND.getExceptionMessage()));
        reply.delete();
        teamPost.reduceReplyCnt();
    }

    public ReplyDto.ResponseRepliesRetrieve getReplies(Long nextIdx, Long commentPk, String memberId){
        List<Reply> replies = replyRepository.findRepliesByNextIdx(nextIdx, commentPk);
        Team team = commentRepository.findTeam(commentPk)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.TEAM_NOT_FOUND.getExceptionMessage()));
        TeamMember requester = teamMemberRepository.findByTeamAndMember_MemberId(team, memberId)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.TEAM_MEMBER_NOT_FOUND.getExceptionMessage()));

        LinkedHashMap<Long, ReplyDto.ReplyInfo> repliesInfo = new LinkedHashMap<>();
        replies.forEach(reply -> repliesInfo.put(reply.getPk(), ReplyDto.ReplyInfo.builder()
                .imgUrl(s3ImgService.getTeamMemberPreSignedUrl(reply.getWriter().getProfileImg()))
                .nickname(reply.getWriter().getNickname())
                .content(reply.getContent())
                .createDate(reply.getCreatedTime().toLocalDate())
                .isWriter(reply.isWriter(requester))
                .isDeleted(reply.isDeleted())
                .isLocked(reply.isLocked())
                .build()));

        return ReplyDto.ResponseRepliesRetrieve.builder()
                .repliesInfo(repliesInfo)
                .nextUrl(createGetRepliesNextUrl(replies, commentPk))
                .build();
    }

    private String createGetRepliesNextUrl(List<Reply> replies, Long commentPk){
        Optional<Long> minReplyPk = replies.stream().map(Reply::getPk).min(Comparator.naturalOrder());
        String nextUrl = null;
        if (isExistNextReply(minReplyPk, commentPk)){
            nextUrl = serverIp + REPLY_RETRIEVE_PATH + createParametersUrl(commentPk, minReplyPk);
        }

        return nextUrl;
    }

    private boolean isExistNextReply(Optional<Long> minReplyPk, Long commentPk){
        boolean hasNext = false;
        if (minReplyPk.isPresent()){
            hasNext = replyRepository.existsByPkLessThanAndCommentPk(minReplyPk.get(), commentPk);
        }
        return hasNext;
    }

    private String createParametersUrl(Long commentPk, Optional<Long> minReplyPk) {
        HashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("nextIdx", minReplyPk.get() - 1);
        parameters.put("commentPk", commentPk);

        return "?" + parameters.entrySet().stream()
                .map(parameter -> parameter.getKey() + "=" + parameter.getValue())
                .collect(Collectors.joining("&"));
    }
}
