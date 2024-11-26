package com.lljk.fitfinder;

import com.lljk.fitfinder.entity.Facility;
import com.lljk.fitfinder.service.FacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FitfinderApplication implements CommandLineRunner {

	@Autowired
	private FacilityService facilityService;

	public static void main(String[] args) {
		SpringApplication.run(FitfinderApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		String filePath = "src/main/resources/FACILITY_LIST.csv";
		facilityService.loadFacilitiesFromCsv(filePath);
	}
}
