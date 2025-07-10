package com.ibs.vi.util;

import com.ibs.vi.model.RouteLeg;
import com.ibs.vi.model.Segment;
import com.ibs.vi.model.SegmentWithLayover;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
}
