package com.wemingle.core.domain.img.entity;

import com.wemingle.core.domain.post.entity.TeamPost;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class TeamPostImg {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk")
    private Long pk;

    @NotNull
    @Column(name = "IMG_NAME")
    private String imgName;

    @NotNull
    @Column(name = "IMG_PATH")
    private String imgPath;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_POST")
    private TeamPost teamPost;
}
