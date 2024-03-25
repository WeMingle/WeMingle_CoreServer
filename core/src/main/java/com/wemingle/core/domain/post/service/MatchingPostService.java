package com.wemingle.core.domain.post.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wemingle.core.domain.img.service.S3ImgService;
import com.wemingle.core.domain.matching.entity.Matching;
import com.wemingle.core.domain.matching.repository.MatchingRepository;
import com.wemingle.core.domain.member.entity.Member;
import com.wemingle.core.domain.member.repository.MemberRepository;
import com.wemingle.core.domain.post.dto.MatchingPostDto;
import com.wemingle.core.domain.post.entity.MatchingPost;
import com.wemingle.core.domain.post.entity.MatchingPostArea;
import com.wemingle.core.domain.post.entity.abillity.Ability;
import com.wemingle.core.domain.post.entity.gender.Gender;
import com.wemingle.core.domain.post.entity.recruitertype.RecruiterType;
import com.wemingle.core.domain.post.repository.MatchingPostRepository;
import com.wemingle.core.domain.team.entity.Team;
import com.wemingle.core.domain.team.entity.TeamMember;
import com.wemingle.core.domain.team.entity.recruitmenttype.RecruitmentType;
import com.wemingle.core.domain.team.repository.TeamMemberRepository;
import com.wemingle.core.domain.team.repository.TeamRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static com.wemingle.core.global.exceptionmessage.ExceptionMessage.TEAM_MEMBER_NOT_FOUND;
import static com.wemingle.core.global.exceptionmessage.ExceptionMessage.TEAM_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchingPostService {
    private final MatchingPostRepository matchingPostRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final MemberRepository memberRepository;
    private final MatchingRepository matchingRepository;
    private final S3ImgService s3ImgService;

    List<ObjectNode> getFilteredMatchingPost(Long nextIdx,
                                             RecruitmentType recruitmentType,
                                             Ability ability,
                                             Gender gender,
                                             RecruiterType recruiterType,
                                             List<MatchingPostArea> areaList,
                                             LocalDate dateFilter,
                                             Boolean excludeExpired){


        List<MatchingPost> filteredMatchingPost = matchingPostRepository.findFilteredMatchingPost(
                nextIdx,
                recruitmentType,
                ability,
                gender,
                recruiterType,
                areaList,
                excludeExpired == null ? null : LocalDate.now(),
                dateFilter,
                PageRequest.of(0, 30)
        );

        ObjectNode objectNode = new ObjectMapper().createObjectNode();

        return filteredMatchingPost.stream().map(post -> objectNode.put(post.getPk().toString(),
                MatchingPostDto.ResponseMatchingPostDto.builder()
                        .writer(post.getWriter().getTeam().getTeamName())
                        .matchingDate(post.getMatchingDate())
                        .areaList(post.getAreaList())
                        .ability(post.getAbility())
                        .isLocationConsensusPossible(post.isLocationConsensusPossible())
                        .contents(post.getContent())
                        .recruiterType(post.getRecruiterType())
                        .profilePicUrl(post.getRecruiterType().equals(RecruiterType.TEAM) ? s3ImgService.getGroupProfilePicUrl(post.getTeam().getProfileImgId()) : s3ImgService.getMemberProfilePicUrl(post.getTeam().getProfileImgId()))
                        .matchingCnt(post.getCompletedMatchingCnt())
                        .build().toString()
        )).toList();

    }

    @Transactional
    public void createMatchingPost(MatchingPostDto.CreateMatchingPostDto createMatchingPostDto, String writerId){//todo 월욜날 할 컨트롤러에 써먹어야함,,
        RecruiterType recruiterType = createMatchingPostDto.getRecruiterType();
        Long teamPk = createMatchingPostDto.getTeamPk();
        List<String> participantsId = createMatchingPostDto.getParticipantsId();

        Team team = teamRepository.findById(teamPk).orElseThrow(() -> new EntityNotFoundException(TEAM_NOT_FOUND.getExceptionMessage()));
        TeamMember writerInTeam = teamMemberRepository.findByTeamAndMember_MemberId(team, writerId)
                .orElseThrow(() -> new EntityNotFoundException(TEAM_MEMBER_NOT_FOUND.getExceptionMessage()));

        MatchingPost matchingPost = createMatchingPostDto.of(team, writerInTeam);
        matchingPostRepository.save(matchingPost);

        if (isExistTeamParticipant(recruiterType, participantsId)){
            List<Member> memberList = memberRepository.findByMemberIdIn(participantsId);
            createParticipants(team, memberList, matchingPost);
        }
    }

    private void createParticipants(Team team, List<Member> memberList, MatchingPost matchingPost) {
        List<Matching> matchingParticipantList = memberList.stream().map(member -> Matching.builder()
                        .matchingPost(matchingPost)
                        .member(member)
                        .team(team)
                        .build())
                .toList();

        matchingRepository.saveAll(matchingParticipantList);
    }

    private boolean isExistTeamParticipant(RecruiterType recruiterType, List<String> participantsPk) {
        return recruiterType.equals(RecruiterType.TEAM) && !participantsPk.isEmpty();
    }
}
