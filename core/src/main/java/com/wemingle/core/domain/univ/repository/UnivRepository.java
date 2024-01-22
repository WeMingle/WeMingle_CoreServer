package com.wemingle.core.domain.univ.repository;

import com.wemingle.core.domain.univ.entity.UnivEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UnivRepository extends JpaRepository<UnivEntity,Long> {
    Optional<UnivEntity> findByUnivName(String univName);

    Optional<UnivEntity> findByDomain(String domain);
}
