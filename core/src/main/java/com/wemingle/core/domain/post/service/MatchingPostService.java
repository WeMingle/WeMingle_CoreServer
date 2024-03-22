package com.wemingle.core.domain.post.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wemingle.core.domain.img.service.S3ImgService;
import com.wemingle.core.domain.post.entity.MatchingPost;
import com.wemingle.core.domain.post.entity.abillity.Ability;
import com.wemingle.core.domain.post.entity.area.AreaName;
import com.wemingle.core.domain.post.entity.gender.Gender;
import com.wemingle.core.domain.post.entity.recruitertype.RecruiterType;
import com.wemingle.core.domain.post.repository.MatchingPostRepository;
import com.wemingle.core.domain.post.dto.MatchingPostDto;
import com.wemingle.core.domain.team.entity.recruitmenttype.RecruitmentType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchingPostService {
    private final MatchingPostRepository matchingPostRepository;
    private final S3ImgService s3ImgService;

    List<ObjectNode> getFilteredMatchingPost(Long nextIdx,
                                               RecruitmentType recruitmentType,
                                               Ability ability,
                                               Gender gender,
                                               RecruiterType recruiterType,
                                               AreaName location,
                                               Boolean excludeExpired){


        List<MatchingPost> filteredMatchingPost = matchingPostRepository.findFilteredMatchingPost(
                nextIdx,
                recruitmentType == null ? null : recruitmentType.name(),
                ability == null ? null : ability.name(),
                gender == null ? null : gender.name(),
                recruiterType == null ? null : recruiterType.name(),
                location == null ? null : location.name(),
                excludeExpired == null ? null : LocalDate.now(),
                PageRequest.of(0, 30)
        );

        ObjectNode objectNode = new ObjectMapper().createObjectNode();

        return filteredMatchingPost.stream().map(post -> objectNode.put(post.getPk().toString(),
                com.wemingle.core.domain.post.entity.dto.MatchingPostDto.ResponseMatchingPostDto.builder()
                        .writer(post.getWriter().getTeam().getTeamName())
                        .matchingDate(post.getMatchingDate())
//                        .areaName(post.getAreaName())  //todo areaName이 복수 선택으로 바뀜으로써 이를 관리하는 MatchingPostArea 테이블 추가하여 변경 필요
                        .ability(post.getAbility())
                        .isLocationConsensusPossible(post.isLocationConsensusPossible())
                        .contents(post.getContent())
                        .recruiterType(post.getRecruiterType())
                        .profilePicUrl(post.getRecruiterType().equals(RecruiterType.TEAM) ? s3ImgService.getGroupProfilePicUrl(post.getTeam().getProfileImgId()) : s3ImgService.getMemberProfilePicUrl(post.getTeam().getProfileImgId()))
                        .matchingCnt(post.getCompletedMatchingCnt())
                        .build().toString()
        )).toList();

    }

    public void createMatchingPost(MatchingPostDto.CreateMatchingPostDto createMatchingPostDto){

    }
}
