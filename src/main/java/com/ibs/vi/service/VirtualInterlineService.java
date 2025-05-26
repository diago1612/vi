package com.ibs.vi.service;

import com.ibs.vi.model.Flight;
import java.time.LocalDate;
import java.util.List;


public interface VirtualInterlineService {

    String getHealthStatus();

    List<List<Flight>> generateItineraries(String origin, String destination, LocalDate departureDate) throws Exception;

}
