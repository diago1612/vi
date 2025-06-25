package com.ibs.vi.service;

import com.ibs.vi.model.Segment;

import java.util.List;

public interface VIRouteLogic {
    List<Segment> viSegmentDetails(List<String> keys, String... index);
}
