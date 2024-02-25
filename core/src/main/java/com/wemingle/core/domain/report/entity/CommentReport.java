package com.wemingle.core.domain.report.entity;

import com.wemingle.core.domain.comment.entity.Comment;
import com.wemingle.core.domain.common.entity.BaseEntity;
import com.wemingle.core.domain.group.entity.GroupMember;
import com.wemingle.core.domain.report.entity.reportinfo.ReportInfo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class CommentReport extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK")
    private Long pk;

    @NotNull
    @Embedded
    private ReportInfo reportInfo;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMMENT")
    private Comment comment;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_MEMBER")
    private GroupMember groupMember;


}
