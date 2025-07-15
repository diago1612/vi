package com.ibs.vi.service;

import com.ibs.vi.model.Flights;
import com.ibs.vi.model.Segment;
import com.ibs.vi.model.SegmentWithLayover;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;


public interface VIService {

    List<Flights> fetchVIResult(String origin, String destination, LocalDate departureDate, int pax) throws Exception;

    <T> List<T> viSegmentDetails(Map<String, List<String>> keyMap, Function<Segment, T> segmentMapper);

    List<List<SegmentWithLayover>> buildFilteredSegmentCombinations(String origin, String destination, LocalDate departureDate, int pax) throws Exception;
}
