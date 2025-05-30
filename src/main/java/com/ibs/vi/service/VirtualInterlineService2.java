package com.ibs.vi.service;

import com.ibs.vi.model.Flight;
import com.ibs.vi.model.Flights;

import java.time.LocalDate;
import java.util.List;

public interface VirtualInterlineService2 {
    String getHealthStatus();

    List<List<Flight>> generateItineraries(String origin, String destination, LocalDate departureDate, int pax) throws Exception;

    List<Flights> generateNewItineraries(String origin, String destination, LocalDate departureDate, int pax) throws Exception;


}
