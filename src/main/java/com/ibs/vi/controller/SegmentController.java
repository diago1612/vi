package com.ibs.vi.controller;

import com.ibs.vi.model.Route;
import com.ibs.vi.model.Segment;
import com.ibs.vi.service.RouteService;
import com.ibs.vi.view.AirlineView;
import com.ibs.vi.view.BasicResponseView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("segments")
public class SegmentController {
    @Autowired
    @Qualifier("segmentManagementService")
    private RouteService routeService;

    @PostMapping
    public BasicResponseView create(@RequestBody Segment segment) {
        return routeService.save(segment);
    }

    @GetMapping
    public List<AirlineView> getAllRoutes() {
        return routeService.getAll();
    }
}
