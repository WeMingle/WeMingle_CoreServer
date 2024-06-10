package com.wemingle.core.domain.post.service;

import com.wemingle.core.domain.comment.service.CommentService;
import com.wemingle.core.domain.team.entity.TeamMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class TeamPostCommentService {
    private final TeamPostService teamPostService;
    private final CommentService commentService;

    @Transactional
    public void deleteTeamPostByTeamMember(TeamMember teamMember) {

    }
}
