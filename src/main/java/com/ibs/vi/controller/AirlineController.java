package com.ibs.vi.controller;


import com.ibs.vi.model.Airline;
import com.ibs.vi.service.RouteService;
import com.ibs.vi.view.AirlineView;
import com.ibs.vi.view.BasicResponseView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("airline")
public class AirlineController {

    @Autowired
    @Qualifier("airlineManagementService")
    private RouteService routeService;

    @PostMapping
    public BasicResponseView create(@RequestBody Airline airline) {
        return routeService.save(airline);
    }

    @GetMapping("{key}")
    public AirlineView getRouteByKey(@PathVariable String key) {
        return (AirlineView) routeService.getByKey(key);
    }

    @GetMapping
    public List<AirlineView> getAllRoutes() {
        return routeService.getAll();
    }

    @PutMapping("{key}")
    public AirlineView updateRouteByKey(@PathVariable String key, @RequestBody Airline airline) {
        return (AirlineView)routeService.updateByKey(key, airline);
    }

    @DeleteMapping("{key}")
    public BasicResponseView deleteRouteByKey(@PathVariable String key) {
        return routeService.deleteByKey(key);
    }
    @DeleteMapping("delete/all")
    public BasicResponseView deleteRoutes() {
        return routeService.deleteAll();
    }

}
