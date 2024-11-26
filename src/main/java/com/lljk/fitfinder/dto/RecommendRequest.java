package com.lljk.fitfinder.dto;

import lombok.Data;

@Data
public class RecommendRequest {
    private Double latitude;
    private Double longitude;
    private String activity;
}
