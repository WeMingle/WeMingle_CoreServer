package com.wemingle.core.domain.member.entity;

import com.wemingle.core.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PolicyTerms extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk")
    private Long pk;

    @NotNull
    @Column(name = "POLICY_1")
    private boolean policy1;

    @NotNull
    @Column(name = "POLICY_2")
    private boolean policy2;

    @NotNull
    @Column(name = "POLICY_3")
    private boolean policy3;

    @NotNull
    @Column(name = "AGREE_TO_LOCATION_BASED_SERVICES")
    private boolean agreeToLocationBasedServices;

    @NotNull
    @Column(name = "AGREE_TO_RECEIVE_MARKETING_INFORMATION")
    private boolean agreeToReceiveMarketingInformation;

    @Builder
    public PolicyTerms(boolean agreeToLocationBasedServices, boolean agreeToReceiveMarketingInformation) {
        this.policy1 = true;
        this.policy2 = true;
        this.policy3 = true;
        this.agreeToLocationBasedServices = agreeToLocationBasedServices;
        this.agreeToReceiveMarketingInformation = agreeToReceiveMarketingInformation;
    }
}
