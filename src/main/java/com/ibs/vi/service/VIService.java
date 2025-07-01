package com.ibs.vi.service;

import com.ibs.vi.model.Segment;

import java.time.LocalDate;
import java.util.List;


public interface VIService {

    List<List<Segment>> generateVIItineraries(String origin, String destination, LocalDate departureDate, int pax) throws Exception;
    List<Segment> viSegmentDetails(String[] keys, String... airlineCodes);

}
