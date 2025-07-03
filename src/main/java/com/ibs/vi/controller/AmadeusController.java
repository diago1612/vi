package com.ibs.vi.controller;

import com.ibs.vi.model.Segment;
import com.ibs.vi.service.AmadeusApiService;
import com.ibs.vi.view.BasicResponseView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/amadeus")
public class AmadeusController {

    private final AmadeusApiService apiService;

    public AmadeusController(AmadeusApiService apiService) {
        this.apiService = apiService;
    }

    @GetMapping("/flight-offers")
    public String getFlightOffers() {
        return apiService.getFlightOffers();
    }

    @PostMapping("/fetch")
    public BasicResponseView addSegments() {
        return apiService.fetchAndSaveSegments();
    }
}