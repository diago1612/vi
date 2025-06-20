package com.ibs.vi.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibs.vi.model.Flight;
import com.ibs.vi.model.FlightPoint;
import com.ibs.vi.model.Flights;
import com.ibs.vi.model.Layover;
import com.ibs.vi.service.VirtualInterlineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VirtualInterlineImplementation implements VirtualInterlineService {

    private static final Duration MIN_LAYOVER = Duration.ofHours(1);

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
    public List<List<Flight>> generateItineraries(String origin, String destination, LocalDate departureDate, int pax) throws Exception {
        List<Flight> flights = loadFlightsFromJson();
        List<List<Flight>> itineraries = new ArrayList<>();

        for (Flight flight : flights) {
            if (flight.departureAirport.equalsIgnoreCase(origin)
                    && flight.departureTime.toLocalDate().equals(departureDate)
                     && flight.availableSeats>=pax) {
                List<Flight> path = new ArrayList<>();
                path.add(flight);
                buildItineraries(path, flights, itineraries, destination, pax);
            }
        }
        return itineraries;
    }

    private void buildItineraries(List<Flight> currentPath, List<Flight> allFlights,
                                  List<List<Flight>> allItineraries, String destination, int pax) {
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
            if (nextFlight.availableSeats < pax) continue;

            boolean isExp = nextFlight.getFlightNumber().toUpperCase().contains("-EXP");

            // If EXP: force same airline across entire path
            if (isExp) {
                boolean allSameAirline = currentPath.stream()
                        .allMatch(f -> f.getAirline().equalsIgnoreCase(nextFlight.getAirline()));
                if (!allSameAirline) continue;
            }

            List<Flight> newPath = new ArrayList<>(currentPath);
            newPath.add(nextFlight);
            buildItineraries(newPath, allFlights, allItineraries, destination, pax);
        }
    }


    @Override
    public List<Flights> generateNewItineraries(String origin, String destination, LocalDate departureDate, int pax) throws Exception {
        List<List<Flight>> rawData = generateItineraries(origin, destination, departureDate, pax);
        return convertToNewFormat(rawData);
    }

    public List<Flights> convertToNewFormat(List<List<Flight>> rawItineraries) {
        List<Flights> result = new ArrayList<>();
        int idCounter = 1;

        for (List<Flight> itinerary : rawItineraries) {
            if (itinerary.isEmpty()) continue;

            Flight first = itinerary.get(0);
            Flight last = itinerary.get(itinerary.size() - 1);

            Flights flight = new Flights();
            flight.setId(idCounter++);

            // Departure
            LocalDateTime depTime = first.getDepartureTime();
            FlightPoint departure = new FlightPoint();
            departure.setTime(depTime.format(DateTimeFormatter.ofPattern("HH:mm")));
            departure.setDate(depTime.format(DateTimeFormatter.ofPattern("d MMM")));
            departure.setAirport(first.getDepartureAirport());
            departure.setAirportName(first.getDepartureAirportName()); 
            flight.setDeparture(departure);

            // Arrival
            LocalDateTime arrTime = last.getArrivalTime();
            FlightPoint arrival = new FlightPoint();
            arrival.setTime(arrTime.format(DateTimeFormatter.ofPattern("HH:mm")));
            arrival.setDate(arrTime.format(DateTimeFormatter.ofPattern("d MMM")));
            arrival.setAirport(last.getArrivalAirport());
            arrival.setAirportName(last.getArrivalAirportName());
            flight.setArrival(arrival);

            // General Info
            flight.setStops(itinerary.size() - 1);
            flight.setDuration(calculateDuration(depTime, arrTime));
            flight.setFareType("Included: personal item, cabin bag");
            double totalPrice = itinerary.stream()
                    .mapToDouble(Flight::getFare)
                    .sum();
            flight.setPrice(totalPrice);
            flight.setCurrency("USD");
            flight.setSegments(itinerary);
            for (int i = 1; i < itinerary.size(); i++) {
                Flight prev = itinerary.get(i - 1);
                Flight curr = itinerary.get(i);

                boolean isDifferentAirline = !prev.getAirline().equalsIgnoreCase(curr.getAirline());
                boolean isExpConnection = curr.getFlightNumber().toUpperCase().contains("-EXP");
                boolean isSelfTransfer = isDifferentAirline && !isExpConnection;

                Layover layover = new Layover();
                layover.setDuration(calculateDuration(prev.getArrivalTime(), curr.getDepartureTime()));
                layover.setSelfTransfer(isSelfTransfer);

                curr.setLayover(layover);
            }

            List<String> airlineNames = itinerary.stream()
                    .map(Flight::getAirline)
                    .distinct()
                    .collect(Collectors.toList());
            flight.setAirlines(airlineNames);

            result.add(flight);
        }
        result.sort(Comparator.comparingDouble(Flights::getPrice));
        return result;
    }

    private String calculateDuration(LocalDateTime start, LocalDateTime end) {
        Duration duration = Duration.between(start, end);
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        return hours + "h " + minutes + "m";
    }

    private List<Flight> loadFlightsFromJson() throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream("data/flights-dummy.json");
        Map<?, ?> map = mapper.readValue(is, Map.class);
        List<?> flightList = (List<?>) map.get("flights");
        return Arrays.asList(mapper.convertValue(flightList, Flight[].class));
    }
}
