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

    @GetMapping("/max-legs")
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
    public BasicResponseView setDepartureWindowDays(@RequestParam int value) {
        return viConfigService.setDepartureWindowDays(value);
    }
}
