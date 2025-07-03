package com.ibs.vi.controller;

import com.ibs.vi.service.AmadeusApiService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}