
package com.ibs.vi.controller;

import com.ibs.vi.model.Route;
import com.ibs.vi.service.RouteService;
import com.ibs.vi.view.BasicResponseView;
import com.ibs.vi.view.RouteView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *
 * @author jithin123
 */
@RestController
@RequestMapping("routes")
public class RouteController {

    @Autowired
    private RouteService routeService;

    @PostMapping
    public BasicResponseView create(@RequestBody Route route) {
        return routeService.save(route);
    }

    @GetMapping("{key}")
    public RouteView getRouteByKey(@PathVariable String key) {
        return (RouteView) routeService.getByKey(key);
    }

    @GetMapping
    public List<RouteView> getAllRoutes() {
        return routeService.getAll();
    }

    @PutMapping("{key}")
    public RouteView updateRouteByKey(@PathVariable String key, @RequestBody Route route) {
        return (RouteView)routeService.updateByKey(key, route);
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
