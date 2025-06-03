package com.ibs.vi.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibs.vi.model.Flight;
import com.ibs.vi.model.FlightPoint;
import com.ibs.vi.model.Flights;
import com.ibs.vi.model.Layover;
import com.ibs.vi.service.VirtualInterlineService2;
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
public class VirtualInterlineImplementation2 implements VirtualInterlineService2 {

    private static final Duration MIN_LAYOVER = Duration.ofHours(1);
    private final ObjectMapper mapper;

    @Autowired
    public VirtualInterlineImplementation2(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public String getHealthStatus() {
        return "Application is healthy";
    }

    @Override
    public List<List<Flight>> generateItineraries(String origin, String destination, LocalDate departureDate, int pax) throws Exception {
        List<List<Flight>> allRoutes = loadRoutesFromJson();
        List<List<Flight>> validItineraries = new ArrayList<>();

        for (List<Flight> route : allRoutes) {
            if (route.isEmpty()) continue;

            Flight first = route.get(0);
            Flight last = route.get(route.size() - 1);

            boolean originMatches = first.getDepartureAirport().equalsIgnoreCase(origin);
            boolean destinationMatches = last.getArrivalAirport().equalsIgnoreCase(destination);
            boolean dateMatches = first.getDepartureTime().toLocalDate().equals(departureDate);
            boolean allHaveSeats = route.stream().allMatch(f -> f.getAvailableSeats() >= pax);

            if (originMatches && destinationMatches && dateMatches && allHaveSeats) {
                if (isValidRoute(route, pax)) {
                    validItineraries.add(route);
                }
            }
        }

        return validItineraries;
    }

    private boolean isValidRoute(List<Flight> route, int pax) {
        for (int i = 1; i < route.size(); i++) {
            Flight prev = route.get(i - 1);
            Flight curr = route.get(i);

            Duration layover = Duration.between(prev.getArrivalTime(), curr.getDepartureTime());
            if (layover.compareTo(MIN_LAYOVER) < 0) return false;

            if (curr.getAvailableSeats() < pax) return false;
        }
        return true;
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
            double totalPrice = itinerary.stream().mapToDouble(Flight::getFare).sum();
            flight.setPrice(totalPrice);
            flight.setCurrency("USD");
           // flight.setSegments(itinerary);

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
            // Remove "-EXP" suffix from flight numbers before returning
            List<Flight> sanitizedItinerary = itinerary.stream()
                    .map(flightSegment -> {
                        Flight sanitized = new Flight(flightSegment);
                        String sanitizedFlightNumber = sanitized.getFlightNumber().replace("-EXP", "");
                        sanitized.setFlightNumber(sanitizedFlightNumber);
                        return sanitized;
                    })
                    .collect(Collectors.toList());
            flight.setSegments(sanitizedItinerary);

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

    private List<List<Flight>> loadRoutesFromJson() throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream("data/flights.json");
        Map<?, ?> map = mapper.readValue(is, Map.class);
        List<?> rawRoutes = (List<?>) map.get("flights");

        List<List<Flight>> routes = new ArrayList<>();
        for (Object routeObj : rawRoutes) {
            List<?> rawFlights = (List<?>) routeObj;
            List<Flight> route = Arrays.asList(mapper.convertValue(rawFlights, Flight[].class));
            routes.add(route);
        }
        return routes;
    }
}
