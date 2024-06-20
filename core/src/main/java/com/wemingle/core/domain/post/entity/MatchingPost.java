package com.wemingle.core.domain.post.entity;

import com.wemingle.core.domain.category.sports.entity.sportstype.SportsType;
import com.wemingle.core.domain.common.entity.BaseEntity;
import com.wemingle.core.domain.member.entity.Member;
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
    @Column(name = "EXPIRY_DATE")
    private LocalDate expiryDate;//마감

    @NotNull
    @Column(name = "MATCHING_DATE")
    private LocalDate matchingDate;

    @NotNull
    @Column(name = "LOCATION_NAME")
    private String locationName; // 매칭 장소 이름
    @Column(name = "DOU")
    private String dou;
    @Column(name = "SI")
    private String si;
    @Column(name = "GUN")
    private String gun;
    @Column(name = "GU")
    private String gu;
    @Column(name = "DONG")
    private String dong;
    @Column(name = "EUP")
    private String eup;
    @Column(name = "MYEON")
    private String myeon;
    @Column(name = "RI")
    private String ri;

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
    @Column(name = "MY_CAPACITY_COUNT")
    private int myCapacityCount;

    @NotNull
    @Column(name = "CAPACITY_LIMIT")
    private int capacityLimit;

    @NotNull
    @Column(name = "IS_LOCATION_CONSENSUS_POSSIBLE")
    private boolean isLocationConsensusPossible;

    @Column(name = "VIEW_CNT")
    private int viewCnt;

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

    @Enumerated(EnumType.STRING)
    private SportsType sportsCategory;

    @NotNull
    @OneToMany(mappedBy = "matchingPost", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<MatchingPostArea> areaList = new ArrayList<>();

    @Builder
    public MatchingPost(LocalDate expiryDate, LocalDate matchingDate, String locationName, String dou, String si, String gun, String gu, String dong, String eup, String myeon, String ri, Double lat, Double lon, String content, int myCapacityCount, int capacityLimit, boolean isLocationConsensusPossible, int viewCnt, Ability ability, Gender gender, RecruitmentType recruitmentType, RecruiterType recruiterType, MatchingStatus matchingStatus, LocationSelectionType locationSelectionType, TeamMember writer, Team team, SportsType sportsCategory) {
        this.expiryDate = expiryDate;
        this.matchingDate = matchingDate;
        this.locationName = locationName;
        this.dou = dou;
        this.si = si;
        this.gun = gun;
        this.gu = gu;
        this.dong = dong;
        this.eup = eup;
        this.myeon = myeon;
        this.ri = ri;
        this.lat = lat;
        this.lon = lon;
        this.content = content;
        this.myCapacityCount = myCapacityCount;
        this.capacityLimit = capacityLimit;
        this.isLocationConsensusPossible = isLocationConsensusPossible;
        this.viewCnt = viewCnt;
        this.ability = ability;
        this.gender = gender;
        this.recruitmentType = recruitmentType;
        this.recruiterType = recruiterType;
        this.matchingStatus = MatchingStatus.PENDING;
        this.locationSelectionType = locationSelectionType;
        this.writer = writer;
        this.team = team;
        this.sportsCategory = sportsCategory;
    }

    public MatchingPost reCreateMatchingPost(){
        return MatchingPost.builder()
                .expiryDate(this.expiryDate)
                .matchingDate(this.matchingDate)
                .locationName(this.locationName)
                .dou(this.dou)
                .si(this.si)
                .gun(this.gun)
                .gu(this.gu)
                .dong(this.dong)
                .eup(this.eup)
                .myeon(this.myeon)
                .ri(this.ri)
                .lat(this.lat)
                .lon(this.lon)
                .content(this.content)
                .capacityLimit(this.capacityLimit)
                .isLocationConsensusPossible(this.isLocationConsensusPossible)
                .viewCnt(this.viewCnt)
                .ability(this.ability)
                .gender(this.gender)
                .recruitmentType(this.recruitmentType)
                .recruiterType(this.recruiterType)
                .locationSelectionType(this.locationSelectionType)
                .writer(this.writer)
                .team(this.team)
                .sportsCategory(this.sportsCategory)
                .build();
    }

    public void putAreaList(List<MatchingPostArea> matchingPostAreaList){
        this.areaList = matchingPostAreaList;
    }

    public void putArea(MatchingPostArea matchingPostArea){
        areaList.add(matchingPostArea);
    }
    public void complete(){
        this.matchingStatus = MatchingStatus.COMPLETE;
    }
    public void updateContent(String content){
        this.content = content;
    }
    public boolean isComplete(){
        return this.matchingStatus.equals(MatchingStatus.COMPLETE);
    }
    public boolean isWriter(Member requester) {
        return getWriter().getMember().equals(requester);
    }
}
