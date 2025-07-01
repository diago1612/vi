package com.ibs.vi.controller;

import com.ibs.vi.serviceImpl.VIConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/config")
public class ConfigController {

    @Autowired
    private VIConfigService viConfigService;

    @GetMapping("/max-legs")
    public ResponseEntity<Integer> getMaxLegs() {
        return ResponseEntity.ok(viConfigService.getMaxLegs());
    }

    @PostMapping("/max-legs")
    public ResponseEntity<String> setMaxLegs(@RequestParam int value) {
        if (value < 1) {
            return ResponseEntity.badRequest().body("Value must be >= 1");
        }

        try {
            viConfigService.updateMaxLegs(value);
            return ResponseEntity.ok("Max legs updated to " + value);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to update max legs: " + e.getMessage());
        }
    }

    @GetMapping("/departure-window-days")
    public ResponseEntity<Integer> getDepartureWindowDays() {
        return ResponseEntity.ok(viConfigService.getDepartureWindowDays());
    }

    @PostMapping("/departure-window-days")
    public ResponseEntity<String> setDepartureWindowDays(@RequestParam int value) {
        if (value < 0) {
            return ResponseEntity.badRequest().body("Value must be >= 0");
        }

        try {
            viConfigService.updateDepartureWindowDays(value);
            return ResponseEntity.ok("Departure window days updated to " + value);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to update departure window: " + e.getMessage());
        }
    }
}
