package com.ibs.vi.controller;

import com.ibs.vi.model.VirtualInterlineDataModel;
import com.ibs.vi.service.VirtualInterlineService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    
   @GetMapping("/flight/search")
    public List<List<VirtualInterlineDataModel>> flightSearch(
            @RequestParam(value = "source", required = true) String source, 
            @RequestParam(value = "destination", required = true) String destination,
            @RequestParam(value = "date", required = true) String date) throws Exception {
        return virtualInterlineService.fetchFlightDetails(source, destination, date);
    } 

}
