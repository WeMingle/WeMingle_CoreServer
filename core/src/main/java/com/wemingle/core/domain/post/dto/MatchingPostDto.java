package com.wemingle.core.domain.post.dto;

import com.wemingle.core.domain.post.entity.MatchingPost;
import com.wemingle.core.domain.post.entity.MatchingPostArea;
import com.wemingle.core.domain.post.entity.abillity.Ability;
import com.wemingle.core.domain.post.entity.area.AreaName;
import com.wemingle.core.domain.post.entity.gender.Gender;
import com.wemingle.core.domain.post.entity.locationselectiontype.LocationSelectionType;
import com.wemingle.core.domain.post.entity.recruitertype.RecruiterType;
import com.wemingle.core.domain.team.entity.Team;
import com.wemingle.core.domain.team.entity.TeamMember;
import com.wemingle.core.domain.team.entity.recruitmenttype.RecruitmentType;
import com.wemingle.core.global.annotation.Essential;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import java.time.LocalDate;
import java.util.List;

public class MatchingPostDto {
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateMatchingPostDto {
        @NotNull
        private LocalDate matchingDate;
        @Essential
        private Double latitude;
        @Essential
        private Double longitude;
        @Essential
        private String locationName;
        @NotNull
        private List<AreaName> areaNameList;
        @NotNull
        private boolean isLocationConsensusPossible;
        @NotNull
        private Ability ability;
        private Gender gender;
        @NotNull
        @Min(value = 1, message = "capacityLimit must be greater than 1")
        private int capacityLimit;
        private List<Long> participantsId; // teamMember pk
        @NotNull
        private LocalDate expiryDate;
        @NotNull
        private RecruiterType recruiterType;
        @NotNull
        private RecruitmentType recruitmentType;
        @NotNull
        private String content;
        @NotNull
        private LocationSelectionType locationSelectionType;

        public MatchingPost of(Team team, TeamMember writer){
            GeometryFactory geometryFactory = new GeometryFactory();
            Coordinate coordinate = new Coordinate(longitude, latitude);
            Point position = geometryFactory.createPoint(coordinate);

            MatchingPost matchingPost = MatchingPost.builder()
                    .matchingDate(matchingDate)
                    .expiryDate(expiryDate)
                    .locationName(locationName)
                    .position(position)
                    .content(content)
                    .capacityLimit(capacityLimit)
                    .isLocationConsensusPossible(isLocationConsensusPossible)
                    .ability(ability)
                    .gender(gender)
                    .recruiterType(recruiterType)
                    .recruitmentType(recruitmentType)
                    .locationSelectionType(locationSelectionType)
                    .writer(writer)
                    .team(team)
                    .build();

            List<MatchingPostArea> areaList = areaNameList.stream().map(areaName -> new MatchingPostArea(areaName, matchingPost)).toList();
            matchingPost.addAreaList(areaList);

            return matchingPost;
        }
    }
}
