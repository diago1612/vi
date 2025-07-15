package com.ibs.vi.service;

import com.ibs.vi.model.Flights;
import com.ibs.vi.model.Segment;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;


public interface VIService {

    List<Flights> generateVIItineraries(String origin, String destination, LocalDate departureDate, int pax) throws Exception;
   // List<Segment> viSegmentDetails(Map<String, List<String>> keyMap);
   <T> List<T> viSegmentDetails(Map<String, List<String>> keyMap, Function<Segment, T> segmentMapper);


}
