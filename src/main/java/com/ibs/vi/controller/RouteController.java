
package com.ibs.vi.controller;

import com.ibs.vi.model.Route;
import com.ibs.vi.service.RouteService;
import com.ibs.vi.view.BasicResponseView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    public Route getRouteByKey(@PathVariable String key) {
        return (Route) routeService.getByKey(key);
    }

    @GetMapping
    public Map<String, Route> getAllRoutes() {
        return routeService.getAll();
    }

    @PutMapping
    public Route updateRouteByKey(@RequestBody Route route) {
        return (Route)routeService.update(route);
    }

    @DeleteMapping("{key}")
    public BasicResponseView deleteRouteByKey(@PathVariable String key) {
        return routeService.deleteByKey(key);
    }
    @DeleteMapping("delete/all")
    public BasicResponseView deleteRoutes() {
        return routeService.delete();
    }
    
}
