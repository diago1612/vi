package com.ibs.vi.controller;

import com.ibs.vi.model.Airport;
import com.ibs.vi.service.RouteService;
import com.ibs.vi.view.AirlineView;
import com.ibs.vi.view.AirportView;
import com.ibs.vi.view.BasicResponseView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("airport")
public class AirportController {

    @Autowired
    @Qualifier("airportManagementService")
    private RouteService routeService;

    @PostMapping
    public BasicResponseView createAirport(@RequestBody Airport airport) {
        return routeService.save(airport);
    }

    @GetMapping("{key}")
    public AirportView getAirportByKey(@PathVariable String key) {
        return (AirportView) routeService.getByKey(key);
    }

    @GetMapping
    public List<AirportView> getAllAirports() {
        return routeService.getAll();
    }

    @PutMapping("{key}")
    public AirportView updateAirportsByKey(@PathVariable String key, @RequestBody Airport airport) {
        return (AirportView)routeService.updateByKey(key, airport);
    }

    @DeleteMapping("{key}")
    public BasicResponseView deleteAirportByKey(@PathVariable String key) {
        return routeService.deleteByKey(key);
    }

    @DeleteMapping("delete/all")
    public BasicResponseView deleteAirports() {
        return routeService.deleteAll();
    }
}
