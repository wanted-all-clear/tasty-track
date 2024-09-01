package com.allclear.tastytrack.domain.region.repository;

import com.allclear.tastytrack.domain.region.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RegionRepository extends JpaRepository<Region, Integer> {


    @Query(value = "SELECT * FROM region r WHERE r.dosi = :dosi AND r.sgg = :sgg LIMIT 1", nativeQuery = true)
    Region findFirstByDosiAndSgg(@Param("dosi") String dosi, @Param("sgg") String sgg);

    @Override
    Optional<Region> findById(Integer integer);

}
