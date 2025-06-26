package com.ibs.vi.controller;

import com.ibs.vi.model.SearchRequest;
import com.ibs.vi.model.Segment;
import com.ibs.vi.service.VIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class VIController {

    public VIService viService;

    @Autowired
    public VIController(VIService viService){
        this.viService = viService;
    }


    @PostMapping("/flights/search-vi")
    public List<Segment> getVIItineraries(@RequestBody SearchRequest request) throws Exception {
        return viService.generateVIItineraries(
                request.getOrigin(),
                request.getDestination(),
                request.getDepartureDate(),
                request.getPax()
        );
    }
}
