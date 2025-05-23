package com.ibs.vi.controller;

import com.ibs.vi.service.VirtualInterlineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
