package com.wemingle.core.domain.comment.entity;

import com.wemingle.core.domain.common.entity.BaseEntity;
import com.wemingle.core.domain.group.entity.GroupMember;
import com.wemingle.core.domain.post.entity.GroupPost;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class Comment extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK")
    private Long pk;

    @NotNull
    @Column(name = "CONTENT", length = 3000)
    private String content;

    @NotNull
    @Column(name = "IS_DELETED")
    private boolean isDeleted;

    @NotNull
    @Column(name = "IS_LOCKED")
    private boolean isLocked;

    @NotNull
    @Column(name = "COMPLAINTS_COUNT")
    private int complaintsCount;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_POST")
    private GroupPost groupPost;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_MEMBER")
    private GroupMember groupMember;
}
