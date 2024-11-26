package com.lljk.fitfinder.repository;

import com.lljk.fitfinder.entity.Facility;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, Long> {
    @Query("SELECT f FROM Facility f WHERE f.name = :name AND f.address = :address")
    Optional<Facility> findByNameAndAddress(String name, String address);
}

