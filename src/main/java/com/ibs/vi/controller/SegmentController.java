package com.ibs.vi.controller;

import com.ibs.vi.model.Segment;
import com.ibs.vi.service.RouteService;
import com.ibs.vi.view.BasicResponseView;
import com.ibs.vi.view.SegmentView;
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
    public BasicResponseView createSegment(@RequestBody Segment segment) {
        return routeService.save(segment);
    }

    @GetMapping("{key}")
    public SegmentView getSegmentByKey(@PathVariable String key, @RequestParam String airline) {
        return (SegmentView) routeService.getByKey(key,airline);
    }

    @GetMapping
    public List<SegmentView> getAllSegments() {
        return routeService.getAll();
    }

    @PutMapping("{key}")
    public SegmentView updateSegmentByKey(@PathVariable String key, @RequestBody Segment segment) {
        return (SegmentView)routeService.updateByKey(key, segment);
    }

    @DeleteMapping("{key}")
    public BasicResponseView deleteSegmentByKey(@PathVariable String key, @RequestParam String airline) {
        return routeService.deleteByKey(key, airline);
    }
    @DeleteMapping("delete/all")
    public BasicResponseView deleteSegments(@RequestParam String airline) {
        return routeService.deleteAll(airline);
    }
}
