package com.ibs.vi.controller;

import com.ibs.vi.serviceImpl.VIConfigService;
import com.ibs.vi.view.BasicResponseView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/config")
public class ConfigController {

    @Autowired
    private VIConfigService viConfigService;

    @GetMapping("/maxLegs")
    public BasicResponseView getMaxLegs() {
        return viConfigService.getMaxLegs();
    }

    @PostMapping("/maxLegs")
    public BasicResponseView setMaxLegs(@RequestParam int value) {
        return viConfigService.setMaxLegs(value);
    }

    @GetMapping("/departureWindowDays")
    public BasicResponseView getDepartureWindowDays() {
        return viConfigService.getDepartureWindowDays();
    }

    @PostMapping("/departureWindowDays")
    public BasicResponseView setDepartureWindowDays(@RequestParam int value) { return viConfigService.setDepartureWindowDays(value); }

    @PostMapping("/update-interval")
    public BasicResponseView updateInterval(@RequestParam long minutes) { return viConfigService.updateInterval(minutes); }

    @GetMapping("/current-interval")
    public BasicResponseView getCurrentInterval() { return viConfigService.getCurrentInterval(); }
}
