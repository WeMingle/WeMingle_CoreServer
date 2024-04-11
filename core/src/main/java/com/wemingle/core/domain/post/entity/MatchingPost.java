package com.wemingle.core.domain.post.entity;

import com.wemingle.core.domain.common.entity.BaseEntity;
import com.wemingle.core.domain.post.entity.abillity.Ability;
import com.wemingle.core.domain.post.entity.gender.Gender;
import com.wemingle.core.domain.post.entity.locationselectiontype.LocationSelectionType;
import com.wemingle.core.domain.post.entity.matchingstatus.MatchingStatus;
import com.wemingle.core.domain.post.entity.recruitertype.RecruiterType;
import com.wemingle.core.domain.team.entity.Team;
import com.wemingle.core.domain.team.entity.TeamMember;
import com.wemingle.core.domain.team.entity.recruitmenttype.RecruitmentType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchingPost extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk")
    private Long pk;

    @NotNull
    @Column(name = "MATCHING_DATE")
    private LocalDate matchingDate;//매칭이 성사된 날짜

    @NotNull
    @Column(name = "EXPIRY_DATE")
    private LocalDate expiryDate;//마감

    @NotNull
    @Column(name = "LOCATION_NAME")
    private String locationName; // 매칭 장소 이름

    @NotNull
    @Column(name = "LAT")
    private Double lat;

    @NotNull
    @Column(name = "LON")
    private Double lon;

    @NotNull
    @Column(name = "CONTENT", length = 3000)
    private String content;

    @NotNull
    @Column(name = "CAPACITY_LIMIT")
    private int capacityLimit;

    @NotNull
    @Column(name = "IS_LOCATION_CONSENSUS_POSSIBLE")
    private boolean isLocationConsensusPossible;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Ability ability;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @NotNull
    @Enumerated(EnumType.STRING)
    private RecruitmentType recruitmentType;

    @NotNull
    @Enumerated(EnumType.STRING)
    private RecruiterType recruiterType;

    @NotNull
    @Enumerated(EnumType.STRING)
    private MatchingStatus matchingStatus;

    @NotNull
    @Enumerated(EnumType.STRING)
    private LocationSelectionType locationSelectionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WRITER")
    private TeamMember writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM")
    private Team team;

    @NotNull
    @OneToMany(mappedBy = "matchingPost", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<MatchingPostArea> areaList = new ArrayList<>();

    @Builder
    public MatchingPost(LocalDate matchingDate, LocalDate expiryDate, String locationName, Double lat, Double lon, String content, int capacityLimit, boolean isLocationConsensusPossible, Ability ability, Gender gender, RecruitmentType recruitmentType, RecruiterType recruiterType, LocationSelectionType locationSelectionType, TeamMember writer, Team team) {
        this.matchingDate = matchingDate;
        this.expiryDate = expiryDate;
        this.locationName = locationName;
        this.lat = lat;
        this.lon = lon;
        this.content = content;
        this.capacityLimit = capacityLimit;
        this.isLocationConsensusPossible = isLocationConsensusPossible;
        this.ability = ability;
        this.gender = gender;
        this.recruitmentType = recruitmentType;
        this.recruiterType = recruiterType;
        this.matchingStatus = MatchingStatus.COMPLETE;
        this.locationSelectionType = locationSelectionType;
        this.writer = writer;
        this.team = team;
    }

    public void putAreaList(List<MatchingPostArea> matchingPostAreaList){
        this.areaList = matchingPostAreaList;
    }

    public void putArea(MatchingPostArea matchingPostArea){
        areaList.add(matchingPostArea);
    }
    public void updateForRePost(){
        setCreatedTime(LocalDateTime.now());
        this.matchingStatus = MatchingStatus.PENDING;
    }
}
