package com.ibs.vi.util;

import com.ibs.vi.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class VIUtil {

    private static final Logger log = LoggerFactory.getLogger(VIUtil.class);
    private static final Duration MIN_LAYOVER = Duration.ofHours(1);

    public static Map<String, List<String>> groupByAirline(List<String> zaddKeys) {
        Map<String, List<String>> airlineKeyMap = new HashMap<>();
        for (String entry : zaddKeys) {
            String[] parts = entry.split("\\|");
            if (parts.length == 5) {
                String routeKey = String.join("|", parts[0], parts[1], parts[2], parts[3]); // origin|dest|flightNo|date
                String airline = parts[4]; // airline
                airlineKeyMap.computeIfAbsent(airline, k -> new ArrayList<>()).add(routeKey);
            } else {
                log.warn("Invalid segment key: {}", entry);
            }
        }
        return airlineKeyMap;
    }


    public static List<RouteLeg> buildAllLegs(Map<String, List<String>> airlineKeys) {
        List<RouteLeg> allLegs = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : airlineKeys.entrySet()) {
            String airline = entry.getKey();
            for (String key : entry.getValue()) {
                String[] parts = key.split("\\|");
                if (parts.length == 4) {
                    allLegs.add(new RouteLeg(parts[0], parts[1], parts[2], parts[3], airline));
                } else {
                    log.warn("Invalid routeKey: {}", key);
                }
            }
        }
        return allLegs;
    }


    public static Map<String, List<RouteLeg>> buildgraph(List<RouteLeg> legs) {
        Map<String, List<RouteLeg>> graph = new HashMap<>();
        for (RouteLeg leg : legs) {
            graph.computeIfAbsent(leg.getFrom(), k -> new ArrayList<>()).add(leg);
        }
        return graph;
    }

    public static void findValidPaths(Map<String, List<RouteLeg>> graph, String source, String destination, List<List<RouteLeg>> validPaths, int maxLeg) {
        dfs(graph, source, destination, new HashSet<>(), new ArrayList<>(), validPaths, null, maxLeg);
    }

    private static void dfs(
            Map<String, List<RouteLeg>> graph,
            String currentAirport,
            String destination,
            Set<String> visited,
            List<RouteLeg> path,
            List<List<RouteLeg>> validPaths,
            LocalDate previousDate,
            int maxLeg
    ) {
        if (visited.contains(currentAirport)) return;

        visited.add(currentAirport);

        for (RouteLeg leg : graph.getOrDefault(currentAirport, Collections.emptyList())) {
            LocalDate legDate = LocalDate.parse(leg.getDate());

            if (path.isEmpty() || !legDate.isBefore(previousDate)) {
                path.add(leg);

                if (path.size() > maxLeg) {              // No of legs possible
                    path.remove(path.size() - 1);
                    continue;
                }

                if (leg.getTo().equals(destination)) {
                    validPaths.add(new ArrayList<>(path));
                } else {
                    dfs(graph, leg.getTo(), destination, visited, path, validPaths, legDate, maxLeg);
                }

                path.remove(path.size() - 1);
            }
        }

        visited.remove(currentAirport);
    }

    public static List<RouteLeg> flattenUniqueLegs(List<List<RouteLeg>> paths) {
        return paths.stream()
                .flatMap(List::stream)
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(RouteLeg::key, leg -> leg, (a, b) -> a),
                        map -> new ArrayList<>(map.values())
                ));
    }

    public static List<List<SegmentWithLayover>> generateItineraries(String origin, String destination, LocalDate departureDate, int pax, List<SegmentWithLayover> segments) {
        List<List<SegmentWithLayover>> itineraries = new ArrayList<>();

        for (SegmentWithLayover segment : segments) {
            LocalDateTime departureDateTime = LocalDateTime.parse(segment.getDepartureTime());

            if (segment.getDepartureAirport().equalsIgnoreCase(origin)
                    && departureDateTime.toLocalDate().equals(departureDate)
                    && Integer.parseInt(segment.getAvailableSeats()) >= pax) {

                List<SegmentWithLayover> path = new ArrayList<>();
                path.add(segment);
                buildItineraries(path, segments, itineraries, destination, pax);
            }
        }

        return itineraries;
    }

    private static void buildItineraries(List<SegmentWithLayover> currentPath, List<SegmentWithLayover> allSegments,
                                         List<List<SegmentWithLayover>> allItineraries, String destination, int pax) {

        SegmentWithLayover lastSegment = currentPath.get(currentPath.size() - 1);

        if (lastSegment.getArrivalAirport().equalsIgnoreCase(destination)) {
            allItineraries.add(new ArrayList<>(currentPath));
            return;
        }

        for (SegmentWithLayover nextSegment : allSegments) {
            if (currentPath.contains(nextSegment)) continue;
            if (!lastSegment.getArrivalAirport().equalsIgnoreCase(nextSegment.getDepartureAirport())) continue;

            LocalDateTime lastArrivalTime = LocalDateTime.parse(lastSegment.getArrivalTime());
            LocalDateTime nextDepartureTime = LocalDateTime.parse(nextSegment.getDepartureTime());
            Duration layover = Duration.between(lastArrivalTime, nextDepartureTime);

            if (layover.compareTo(MIN_LAYOVER) < 0) continue;
            if (Integer.parseInt(nextSegment.getAvailableSeats()) < pax) continue;

            List<SegmentWithLayover> newPath = new ArrayList<>(currentPath);
            newPath.add(nextSegment);
            buildItineraries(newPath, allSegments, allItineraries, destination, pax);
        }
    }

    public static void logCombinations(List<List<SegmentWithLayover>> combinations) {
        log.info("Filtered to {} valid combinations", combinations.size());
        for (int i = 0; i < combinations.size(); i++) {
            List<SegmentWithLayover> itinerary = combinations.get(i);
            String path = itinerary.stream()
                    .map(s -> String.format("%s(%s:%s→%s)",
                            s.getFlightNumber(), s.getAirline(),
                            s.getDepartureAirport(), s.getArrivalAirport()))
                    .collect(Collectors.joining(" -> "));
            log.info("Itinerary {}: {}", i + 1, path);
        }
    }

    public static List<Flights> convertToNewFormat(List<List<SegmentWithLayover>> rawItineraries) {
        List<Flights> result = new ArrayList<>();
        int idCounter = 1;

        for (List<SegmentWithLayover> itinerary : rawItineraries) {
            if (itinerary.isEmpty()) continue;

            SegmentWithLayover first = itinerary.get(0);
            SegmentWithLayover last = itinerary.get(itinerary.size() - 1);

            Flights flight = new Flights();
            flight.setId(idCounter++);

            LocalDateTime depTime = LocalDateTime.parse(first.getDepartureTime());
            FlightPoint departure = new FlightPoint();
            departure.setTime(depTime.format(DateTimeFormatter.ofPattern("HH:mm")));
            departure.setDate(depTime.format(DateTimeFormatter.ofPattern("d MMM")));
            departure.setAirport(first.getDepartureAirport());
            departure.setAirportName(first.getDepartureAirportName());
            flight.setDeparture(departure);

            LocalDateTime arrTime = LocalDateTime.parse(last.getArrivalTime());
            FlightPoint arrival = new FlightPoint();
            arrival.setTime(arrTime.format(DateTimeFormatter.ofPattern("HH:mm")));
            arrival.setDate(arrTime.format(DateTimeFormatter.ofPattern("d MMM")));
            arrival.setAirport(last.getArrivalAirport());
            arrival.setAirportName(last.getArrivalAirportName());
            flight.setArrival(arrival);

            flight.setStops(itinerary.size() - 1);
            flight.setDuration(calculateDuration(depTime, arrTime));
            flight.setFareType("Included: personal item, cabin bag");

            double totalPrice = itinerary.stream()
                    .map(SegmentWithLayover::getFare)
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(fare -> !fare.isEmpty())
                    .mapToDouble(fare -> {
                        try {
                            return Double.parseDouble(fare);
                        } catch (NumberFormatException e) {
                            log.warn("Invalid fare value: '{}'. Defaulting to 0.", fare);
                            return 0.0;
                        }
                    })
                    .sum();
            flight.setPrice(totalPrice);
            flight.setCurrency("EUR");
            flight.setSegments(itinerary);

            for (int i = 1; i < itinerary.size(); i++) {
                SegmentWithLayover prev = itinerary.get(i - 1);
                SegmentWithLayover curr = itinerary.get(i);

                boolean isDifferentAirline = !prev.getAirline().equalsIgnoreCase(curr.getAirline());
                boolean isSelfTransfer = isDifferentAirline;

                LocalDateTime prevArrival = LocalDateTime.parse(prev.getArrivalTime());
                LocalDateTime currDeparture = LocalDateTime.parse(curr.getDepartureTime());

                Layover layover = new Layover();
                layover.setDuration(calculateDuration(prevArrival, currDeparture));
                layover.setSelfTransfer(isSelfTransfer);

                curr.setLayover(layover);
            }

            List<String> airlineNames = itinerary.stream()
                    .map(SegmentWithLayover::getAirline)
                    .distinct()
                    .collect(Collectors.toList());
            flight.setAirlines(airlineNames);

            result.add(flight);
        }

        result.sort(Comparator.comparingDouble(Flights::getPrice));
        return result;
    }

    private static String calculateDuration(LocalDateTime start, LocalDateTime end) {
        Duration duration = Duration.between(start, end);
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        return hours + "h " + minutes + "m";
    }

}
