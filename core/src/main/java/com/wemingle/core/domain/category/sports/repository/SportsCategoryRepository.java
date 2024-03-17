package com.wemingle.core.domain.category.sports.repository;

import com.wemingle.core.domain.category.sports.entity.SportsCategory;
import com.wemingle.core.domain.category.sports.entity.sportstype.Sportstype;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SportsCategoryRepository extends JpaRepository<SportsCategory, Long> {
    @Query("select sc from SportsCategory sc where sc.sportsName in :sportsTypes")
    List<SportsCategory> findBySportsTypes(@Param("sportsTypes") List<Sportstype> sportsTypes);
}
