package com.lljk.fitfinder.service;

import com.lljk.fitfinder.entity.Facility;
import com.lljk.fitfinder.repository.FacilityRepository;
import java.nio.charset.StandardCharsets;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileReader;
import java.io.Reader;

@Service
public class FacilityService {
    private static final Logger logger = LoggerFactory.getLogger(FacilityService.class);

    private final FacilityRepository facilityRepository;

    public FacilityService(FacilityRepository facilityRepository) {
        this.facilityRepository = facilityRepository;
    }

    @Transactional
    public void loadFacilitiesFromCsv(String filePath) {

        if(facilityRepository.count() > 0) {
            logger.info("데이터베이스에 이미 데이터가 존재합니다. CSV 처리를 건너뜁니다.");
            return;
        }

        int successCount = 0;
        int skipCount = 0;

        try (Reader reader = new FileReader(filePath, StandardCharsets.UTF_8)) {

            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .withHeader("﻿FCLTY_NM", "FCLTY_SDIV_CD", "FCLTY_FLAG_NM", "INDUTY_CD",
                            "INDUTY_NM", "FCLTY_TY_CD", "FCLTY_TY_NM", "FCLTY_STATE_VALUE",
                            "ROAD_NM_ZIP_NO", "RDNMADR_ONE_NM", "RDNMADR_TWO_NM",
                            "ZIP_NO_VALUE", "FCLTY_ADDR_ONE_NM", "FCLTY_ADDR_TWO_NM",
                            "FCLTY_LO", "FCLTY_LA", "FCLTY_TEL_NO", "FCLTY_HMPG_URL",
                            "CTPRVN_CD", "CTPRVN_NM", "SIGNGU_CD", "SIGNGU_NM",
                            "FCLTY_MANAGE_CTPRVN_CD", "FCLTY_MANAGE_CTPRVN_NM",
                            "FCLTY_MANAGE_SIGNGU_CD", "FCLTY_MANAGE_SIGNGU_NM",
                            "FCLTY_MANAGE_EMD_CD", "FCLTY_MANAGE_EMD_NM",
                            "FCLTY_MANAGE_LI_CD", "FCLTY_MANAGE_LI_NM",
                            "FCLTY_OPER_STLE_VALUE", "POSESN_MBY_CD", "POSESN_MBY_NM",
                            "POSESN_MBY_CTPRVN_CD", "POSESN_MBY_CTPRVN_NM",
                            "POSESN_MBY_SIGNGU_CD", "POSESN_MBY_SIGNGU_NM",
                            "RSPNSBLTY_NM", "RSPNSBLTY_TEL_NO", "NDOR_SDIV_NM",
                            "ADTM_CO", "ACMD_NMPR_CO", "FCLTY_AR_CO", "LVLH_OPN_AT",
                            "LVLH_GMNSM_NM", "UTILIIZA_GRP_NM", "FCLTY_CRTN_STDR_DE",
                            "ALSFC_REGIST_DE", "COMPET_DE", "SSS_DE", "OPER_CLSBIZ_DE",
                            "NATION_ALSFC_AT", "ERDSGN_AT", "ATNM_CHCK_TRGET_AT",
                            "DATA_ORIGIN_FLAG_CD", "DEL_AT", "REGIST_DT", "UPDT_DT")
                    .withFirstRecordAsHeader()
                    .withIgnoreEmptyLines()
                    .withIgnoreSurroundingSpaces()
                    .withQuote('"')
                    .withEscape('\\')
                    .withIgnoreHeaderCase()
                    .withTrim()
                    .withAllowMissingColumnNames()
                    .parse(reader);

            for (CSVRecord record : records) {
                try {
                    if (record.size() < 8) {
                        logger.warn("불완전한 레코드 스킵됨 - 라인: {}", record.getRecordNumber());
                        skipCount++;
                        continue;
                    }

                    Facility facility = new Facility();

                    try {
                        facility.setName(cleanText(record.get("﻿FCLTY_NM")));
                    } catch (Exception e) {
                        facility.setName("Unknown");
                    }

                    try {
                        facility.setType(cleanText(record.get("FCLTY_TY_NM")));
                    } catch (Exception e) {
                        facility.setType("Unknown");
                    }

                    try {
                        facility.setStatus(record.get("FCLTY_STATE_VALUE").contains("정상") ? "Active" : "Inactive");
                    } catch (Exception e) {
                        facility.setStatus("Unknown");
                    }

                    try {
                        String lat = record.get("FCLTY_LA");
                        String lon = record.get("FCLTY_LO");
                        if (lat != null && !lat.trim().isEmpty()) {
                            facility.setLatitude(Double.parseDouble(lat.trim()));
                        }
                        if (lon != null && !lon.trim().isEmpty()) {
                            facility.setLongitude(Double.parseDouble(lon.trim()));
                        }
                    } catch (Exception e) {
                        facility.setLatitude(null);
                        facility.setLongitude(null);
                    }

                    try {
                        facility.setAddress(cleanText(record.get("FCLTY_ADDR_ONE_NM")));
                    } catch (Exception e) {
                        facility.setAddress("Unknown");
                    }

                    try {
                        facility.setIndoorOutdoor(cleanText(record.get("NDOR_SDIV_NM")));
                    } catch (Exception e) {
                        facility.setIndoorOutdoor("Unknown");
                    }

                    try {
                        facility.setTel(cleanText(record.get("FCLTY_TEL_NO")));
                    } catch (Exception e) {
                        facility.setTel("Unknown");
                    }

                    facilityRepository.save(facility);
                    successCount++;

                    if (successCount % 100 == 0) {
                        logger.info("처리된 레코드 수: {}, 스킵된 레코드 수: {}", successCount, skipCount);
                    }

                } catch (Exception e) {
                    skipCount++;
                    logger.warn("레코드 스킵됨 - 라인: {}, 사유: {}", record.getRecordNumber(), e.getMessage());
                }
            }

            logger.info("최종 처리 결과 - 성공: {}, 스킵: {}", successCount, skipCount);

        } catch (Exception e) {
            logger.error("CSV 파일 처리 중 오류 발생: {}", e.getMessage());
        }
    }

    private String cleanText(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "Unknown";
        }

        return value.replaceAll("[\\p{C}\\p{Z}]+", " ").trim();
    }
}