package com.ibs.vi.controller;

import com.ibs.vi.model.Flight;
import com.ibs.vi.service.VirtualInterlineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
public class VirtualInterlineController {

    public final VirtualInterlineService virtualInterlineService;

    @Autowired
    public VirtualInterlineController(VirtualInterlineService virtualInterlineService){
        this.virtualInterlineService = virtualInterlineService;
    }

    @GetMapping("/health")
    public String checkHealth() {
        return virtualInterlineService.getHealthStatus();
    }

    @GetMapping("/flights/search")
    public List<List<Flight>> getItineraries(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate departureDate) throws Exception {

        return virtualInterlineService.generateItineraries(origin, destination, departureDate);
    }

}
