package com.lljk.fitfinder.controller;

import com.lljk.fitfinder.dto.RecommendRequest;
import com.lljk.fitfinder.entity.Facility;
import com.lljk.fitfinder.repository.FacilityRepository;
import com.lljk.fitfinder.service.FacilityService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/facilities")
public class FacilityController {

    private final FacilityService facilityService;

    public FacilityController(FacilityService facilityService) {
        this.facilityService = facilityService;
    }

    @PostMapping("/recommend")
    public ResponseEntity<List<Facility>> recommendFacilities(@RequestBody RecommendRequest request) {
        List<Facility> facilities = facilityService.recommendFacilities(
                request.getLatitude(),
                request.getLongitude(),
                request.getActivity()
        );
        return ResponseEntity.ok(facilities);
    }
}
