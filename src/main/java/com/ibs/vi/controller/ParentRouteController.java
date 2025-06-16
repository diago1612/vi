/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ibs.vi.controller;

import com.ibs.vi.model.Route;
import com.ibs.vi.serviceImpl.ParentRouteServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 *
 * @author jithin123
 */
@RestController
@RequestMapping("/routes")
public class ParentRouteController {
    @Autowired
    private ParentRouteServiceImpl routeService;


    @PostMapping
    public ResponseEntity<String> saveRoute(@RequestBody Route route) {
        routeService.save(route);
        return ResponseEntity.ok("Route saved to Redis");
    }


    @GetMapping("/{key}")
    public ResponseEntity<Route> getRoute(@PathVariable String key) {
        Route route = routeService.getByKey(key);
        if (route != null) {
            return ResponseEntity.ok(route);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Route>> getAllRoutes() {
        return ResponseEntity.ok(routeService.getAll());
    }
}
