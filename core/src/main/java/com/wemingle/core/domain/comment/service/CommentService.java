package com.wemingle.core.domain.comment.service;

import com.wemingle.core.domain.comment.dto.CommentDto;
import com.wemingle.core.domain.comment.entity.Comment;
import com.wemingle.core.domain.comment.repository.CommentRepository;
import com.wemingle.core.domain.post.entity.TeamPost;
import com.wemingle.core.domain.post.repository.TeamPostRepository;
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
public class CommentService {
    private final CommentRepository commentRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamPostRepository teamPostRepository;

    @Transactional
    public void saveComment(CommentDto.RequestCommentSave saveDto, String memberId){
        TeamPost teamPost = teamPostRepository.findById(saveDto.getTeamPostPk())
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.POST_NOT_FOUND.getExceptionMessage()));
        TeamMember requester = teamMemberRepository.findByTeamAndMember_MemberId(teamPost.getTeam(), memberId)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.TEAM_MEMBER_NOT_FOUND.getExceptionMessage()));

        commentRepository.save(Comment.builder()
                .teamPost(teamPost)
                .writer(requester)
                .content(saveDto.getContent())
                .build());

        teamPost.addReplyCnt();
    }

    public boolean isCommentWriter(Long commentPk, String memberId){
        Comment comment = commentRepository.findByIdFetchPost(commentPk)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.COMMENT_NOT_FOUND.getExceptionMessage()));
        TeamMember requester = teamMemberRepository.findByTeamAndMember_MemberId(comment.getTeamPost().getTeam(), memberId)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.TEAM_MEMBER_NOT_FOUND.getExceptionMessage()));

        return comment.isWriter(requester);
    }

    @Transactional
    public void updateComment(CommentDto.RequestCommentUpdate updateDto){
        Comment comment = commentRepository.findById(updateDto.getCommentPk())
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.COMMENT_NOT_FOUND.getExceptionMessage()));

        comment.updateContent(updateDto.getContent());
    }

    @Transactional
    public void deleteComment(CommentDto.RequestCommentDelete deleteDto){
        Comment comment = commentRepository.findByIdFetchPost(deleteDto.getCommentPk())
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.COMMENT_NOT_FOUND.getExceptionMessage()));

        comment.delete();
        comment.getTeamPost().reduceReplyCnt();
    }
}
