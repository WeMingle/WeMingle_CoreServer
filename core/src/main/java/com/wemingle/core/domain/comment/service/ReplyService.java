package com.wemingle.core.domain.comment.service;

import com.wemingle.core.domain.comment.dto.ReplyDto;
import com.wemingle.core.domain.comment.entity.Comment;
import com.wemingle.core.domain.comment.entity.Reply;
import com.wemingle.core.domain.comment.repository.CommentRepository;
import com.wemingle.core.domain.comment.repository.ReplyRepository;
import com.wemingle.core.domain.post.entity.TeamPost;
import com.wemingle.core.domain.team.entity.TeamMember;
import com.wemingle.core.domain.team.repository.TeamMemberRepository;
import com.wemingle.core.global.exceptionmessage.ExceptionMessage;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReplyService {
    private final CommentRepository commentRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final ReplyRepository replyRepository;
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
}
