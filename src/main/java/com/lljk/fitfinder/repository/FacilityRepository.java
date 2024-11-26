package com.lljk.fitfinder.repository;

import com.lljk.fitfinder.entity.Facility;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, Long> {
    @Query("SELECT f FROM Facility f WHERE " +
            "f.latitude BETWEEN :latMin AND :latMax AND " +
            "f.longitude BETWEEN :lonMin AND :lonMax AND " +
            "f.type = :activity")
    List<Facility> findFacilitiesByCriteria(
            @Param("latMin") Double latMin,
            @Param("latMax") Double latMax,
            @Param("lonMin") Double lonMin,
            @Param("lonMax") Double lonMax,
            @Param("activity") String activity
    );
}

