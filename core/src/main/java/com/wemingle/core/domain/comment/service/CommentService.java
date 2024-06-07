package com.wemingle.core.domain.comment.service;

import com.wemingle.core.domain.comment.dto.CommentDto;
import com.wemingle.core.domain.comment.entity.Comment;
import com.wemingle.core.domain.comment.entity.Reply;
import com.wemingle.core.domain.comment.repository.CommentRepository;
import com.wemingle.core.domain.img.service.S3ImgService;
import com.wemingle.core.domain.post.entity.TeamPost;
import com.wemingle.core.domain.post.service.TeamPostService;
import com.wemingle.core.domain.team.entity.Team;
import com.wemingle.core.domain.team.entity.TeamMember;
import com.wemingle.core.domain.team.service.TeamMemberService;
import com.wemingle.core.global.exception.NotWriterException;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;
    private final S3ImgService s3ImgService;
    private final ReplyService replyService;
    private final TeamPostService teamPostService;
    private final TeamMemberService teamMemberService;

    @Value("${wemingle.ip}")
    private String serverIp;

    @Transactional
    public void saveComment(CommentDto.RequestCommentSave saveDto, String memberId){
        TeamPost teamPost = teamPostService.findById(saveDto.getTeamPostId());
        TeamMember requester = teamMemberService.findByTeamAndMember_MemberId(teamPost.getTeam(), memberId);

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
        TeamMember requester = teamMemberService.findByTeamAndMember_MemberId(comment.getTeamPost().getTeam(), memberId);

        return comment.isWriter(requester);
    }

    @Transactional
    public void updateComment(CommentDto.RequestCommentUpdate updateDto, String memberId){
        if (!isCommentWriter(updateDto.getCommentId(), memberId)) {
            throw new NotWriterException();
        }

        Comment comment = commentRepository.findById(updateDto.getCommentId())
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.COMMENT_NOT_FOUND.getExceptionMessage()));

        comment.updateContent(updateDto.getContent());
    }

    @Transactional
    public void deleteComment(CommentDto.RequestCommentDelete deleteDto, String memberId){
        if (!isCommentWriter(deleteDto.getCommentId(), memberId)) {
            throw new NotWriterException();
        }

        Comment comment = commentRepository.findByIdFetchPost(deleteDto.getCommentId())
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.COMMENT_NOT_FOUND.getExceptionMessage()));

        comment.delete();
        comment.getTeamPost().reduceReplyCnt();
    }

    public HashMap<Long, CommentDto.ResponseCommentsInfoRetrieve> getComments(Long nextIdx, Long teamPostId, String memberId){
        List<Comment> comments = commentRepository.findCommentByNextIdx(nextIdx, teamPostId);
        Team team = teamPostService.findTeam(teamPostId);
        TeamMember requester = teamMemberService.findByTeamAndMember_MemberId(team, memberId);

        return createResponseComments(comments, requester);
    }

    public HashMap<Long, CommentDto.ResponseCommentsInfoRetrieve> createResponseComments(List<Comment> comments, TeamMember requester) {
        CommentResponseUtil<Comment> commentResponseUtil = new CommentResponseUtil<>(serverIp);
        List<Comment> filteredComments = commentResponseUtil.removeLastDataIfExceedNextDataMarker(comments);
        List<Reply> repliesByComments = replyService.findRankRepliesByComments(filteredComments.stream().map(Comment::getPk).toList());

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
                .collect(Collectors.toList());
    }
}
