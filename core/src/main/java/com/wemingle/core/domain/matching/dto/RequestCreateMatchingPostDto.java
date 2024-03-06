package com.wemingle.core.domain.matching.dto;

import com.wemingle.core.domain.matching.dto.gengertype.GenderType;
import com.wemingle.core.domain.matching.dto.matchingprocess.MatchingProcessType;
import com.wemingle.core.domain.matching.dto.partnertype.MatchingPartnerType;
import com.wemingle.core.global.annotation.Essential;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@Getter
@ToString
public class RequestCreateMatchingPostDto {
    @NotNull
    LocalDate matchDate;

    @NotNull
    Double latitude;
    @NotNull
    Double longitude;

    @Range(min = 1, max = 3,message = "1~3 사이의 레벨을 선택해주세요")
    @NotNull
    Integer level;

    @NotNull
    GenderType gender;

    @NotNull
    MatchingPartnerType matchingPartner;

    @NotNull
    Map<Integer, List<String>> participation;

    @NotNull
    LocalDate deadline;

    @NotNull
    MatchingProcessType matchingProcess;

    @Max(500)
    @Essential
    String matchingDescription;
}
