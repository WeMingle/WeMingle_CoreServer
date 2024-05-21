package com.wemingle.core.domain.comment.service;

import com.wemingle.core.domain.comment.dto.CommentDto;
import com.wemingle.core.domain.comment.entity.Comment;
import com.wemingle.core.domain.comment.entity.Reply;
import com.wemingle.core.domain.comment.repository.CommentRepository;
import com.wemingle.core.domain.comment.repository.ReplyRepository;
import com.wemingle.core.domain.img.service.S3ImgService;
import com.wemingle.core.domain.post.entity.TeamPost;
import com.wemingle.core.domain.post.repository.TeamPostRepository;
import com.wemingle.core.domain.team.entity.Team;
import com.wemingle.core.domain.team.entity.TeamMember;
import com.wemingle.core.domain.team.repository.TeamMemberRepository;
import com.wemingle.core.global.exceptionmessage.ExceptionMessage;
import com.wemingle.core.global.util.CommentResponseUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamPostRepository teamPostRepository;
    private final S3ImgService s3ImgService;
    private final ReplyService replyService;
    private final ReplyRepository replyRepository;

    @Value("${wemingle.ip}")
    private String serverIp;

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

    public HashMap<Long, CommentDto.ResponseCommentsInfoRetrieve> getComments(Long nextIdx, Long teamPostPk, String memberId){
        List<Comment> comments = commentRepository.findCommentByNextIdx(nextIdx, teamPostPk);
        Team team = teamPostRepository.findTeam(teamPostPk)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.TEAM_NOT_FOUND.getExceptionMessage()));
        TeamMember requester = teamMemberRepository.findByTeamAndMember_MemberId(team, memberId)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.TEAM_MEMBER_NOT_FOUND.getExceptionMessage()));

        return createResponseComments(comments, requester);
    }

    public HashMap<Long, CommentDto.ResponseCommentsInfoRetrieve> createResponseComments(List<Comment> comments, TeamMember requester) {
        CommentResponseUtil<Comment> commentResponseUtil = new CommentResponseUtil<>(serverIp);
        List<Comment> filteredComments = commentResponseUtil.removeLastDataIfExceedNextDataMarker(comments);
        List<Reply> repliesByComments = replyRepository.findRankRepliesByComments(filteredComments.stream()
                .map(Comment::getPk)
                .toList());

        LinkedHashMap<Long, CommentDto.ResponseCommentsInfoRetrieve> commentsInfo = new LinkedHashMap<>();
        filteredComments.forEach(comment -> commentsInfo.put(comment.getPk(), CommentDto.ResponseCommentsInfoRetrieve.builder()
                .imgUrlWithComment(s3ImgService.getTeamMemberPreSignedUrl(comment.getWriter().getProfileImg()))
                .nicknameWithComment(comment.getWriter().getNickname())
                .contentWithComment(comment.getContent())
                .createDateWithComment(comment.getCreatedTime().toLocalDate())
                .isWriterWithComment(comment.isWriter(requester))
                .isDeletedWithComment(comment.isDeleted())
                .isLockedWithComment(comment.isLocked())
                .replies(replyService.createResponseReplies(comment.getPk(), getReplyMatchComment(comment, repliesByComments), requester))
                .build()));

        return commentsInfo;
    }

    public List<Reply> getReplyMatchComment(Comment comment, List<Reply> replies){
        return replies.stream()
                .filter(reply -> reply.getComment().equals(comment))
                .toList();
    }
}
