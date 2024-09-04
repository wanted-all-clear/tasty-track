package com.allclear.tastytrack.domain.region.repository;

import com.allclear.tastytrack.domain.region.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RegionRepository extends JpaRepository<Region, Integer> {

    @Query("SELECT r.sgg FROM Region r")
    List<String> findAllSgg();

    @Query(value = "SELECT * FROM region r WHERE r.dosi = :dosi AND r.sgg = :sgg LIMIT 1", nativeQuery = true)
    Region findFirstByDosiAndSgg(@Param("dosi") String dosi, @Param("sgg") String sgg);

    @Query(value = "SELECT * FROM region r WHERE r.dosi LIKE CONCAT('%', :search, '%') " +
            "OR r.sgg LIKE CONCAT('%', :search, '%')", nativeQuery = true)
    Optional<Region> findByDosiOrSggContaining(@Param("search") String search);

}
