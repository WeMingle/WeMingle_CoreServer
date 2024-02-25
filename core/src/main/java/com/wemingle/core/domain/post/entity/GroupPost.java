package com.wemingle.core.domain.post.entity;

import com.wemingle.core.domain.common.entity.BaseEntity;
import com.wemingle.core.domain.group.entity.Group;
import com.wemingle.core.domain.post.entity.posttype.PostType;
import com.wemingle.core.domain.vote.entity.GroupPostVote;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class GroupPost extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK")
    private Long pk;

    @NotNull
    @Column(name = "TITLE", length = 3000)
    private String title;

    @NotNull
    @Column(name = "CONTENT", length = 3000)
    private String content;

    @NotNull
    @Column(name = "COMPLAINTS_COUNT")
    private int complaintsCount;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PostType postType;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP")
    private Group group;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_POST_VOTE")
    private GroupPostVote groupPostVote;
}
