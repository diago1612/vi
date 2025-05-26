package com.ibs.vi.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibs.vi.model.Flight;
import com.ibs.vi.service.VirtualInterlineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class VirtualInterlineImplementation implements VirtualInterlineService {

    private static final Duration MIN_LAYOVER = Duration.ofHours(2);

    private final ObjectMapper mapper;

    @Autowired
    public VirtualInterlineImplementation(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public String getHealthStatus() {
        return "Application is healthy";
    }

    @Override
    public List<List<Flight>> generateItineraries(String origin, String destination, LocalDate departureDate) throws Exception {
        List<Flight> flights = loadFlightsFromJson();
        List<List<Flight>> itineraries = new ArrayList<>();

        for (Flight flight : flights) {
            if (flight.departureAirport.equalsIgnoreCase(origin)
                    && flight.departureTime.toLocalDate().equals(departureDate)) {
                System.out.println("Matched flight: " + flight);
                List<Flight> path = new ArrayList<>();
                path.add(flight);
                buildItineraries(path, flights, itineraries, destination);
            }
        }

        System.out.println(itineraries);
        return itineraries;
    }

    private void buildItineraries(List<Flight> currentPath, List<Flight> allFlights,
                                  List<List<Flight>> allItineraries, String destination) {
        Flight lastFlight = currentPath.get(currentPath.size() - 1);

        if (lastFlight.arrivalAirport.equalsIgnoreCase(destination)) {
            allItineraries.add(new ArrayList<>(currentPath));
            return;
        }

        for (Flight nextFlight : allFlights) {
            if (currentPath.contains(nextFlight)) continue;
            if (!lastFlight.arrivalAirport.equalsIgnoreCase(nextFlight.departureAirport)) continue;

            Duration layover = Duration.between(lastFlight.arrivalTime, nextFlight.departureTime);
            if (layover.compareTo(MIN_LAYOVER) < 0) continue;

            List<Flight> newPath = new ArrayList<>(currentPath);
            newPath.add(nextFlight);
            buildItineraries(newPath, allFlights, allItineraries, destination);
        }
    }

    private List<Flight> loadFlightsFromJson() throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream("data/flights.json");
        Map<?, ?> map = mapper.readValue(is, Map.class);
        List<?> flightList = (List<?>) map.get("flights");
        return Arrays.asList(mapper.convertValue(flightList, Flight[].class));
    }
}
