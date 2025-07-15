package com.ibs.vi.serviceImpl;

import com.ibs.vi.model.*;
import com.ibs.vi.repository.RedisRepository;
import com.ibs.vi.service.VIService;
import com.ibs.vi.util.RouteUtil;
import com.ibs.vi.util.VIUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class VIImplementation implements VIService {

    @Autowired
    private RedisRepository redisRepository;

    @Autowired
    private PathConfig pathConfig;

    private static final String VI_CACHE_INDEX = "VI_ITINERARIES";

    private static final Logger log = LoggerFactory.getLogger(VIImplementation.class);
    private static final String AIRLINE_INDEX = "AIRLINE";

    @Override
    public <T> List<T> viSegmentDetails(Map<String, List<String>> keyMap, Function<Segment, T> segmentMapper) {
        String[] airlineCodes = {};
        boolean isKeyMapEmpty = keyMap == null || keyMap.isEmpty();

        if (!isKeyMapEmpty) {
            airlineCodes = keyMap.keySet().toArray(new String[0]);
        }

        List<String> activeAirlineCode = redisRepository.values(Airline.class, AIRLINE_INDEX, airlineCodes)
                .stream()
                .filter(Airline::isValid)
                .map(Airline::getAirlineCode)
                .collect(Collectors.toList());

        if (activeAirlineCode.isEmpty()) {
            return Collections.emptyList();
        }

        List<CompletableFuture<List<Segment>>> futures = new ArrayList<>();
        activeAirlineCode.forEach(ac ->
                futures.add(getAllSegmentsByAirportCode(ac, isKeyMapEmpty ? new String[0] : keyMap.get(ac).toArray(new String[0])))
        );

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        return futures.stream()
                .flatMap(future -> {
                    try {
                        return future.join().stream();
                    } catch (Exception e) {
                        log.error("ERROR_FETCHING_AIRLINE", e);
                        return Stream.empty();
                    }
                })
                .map(segmentMapper)
                .collect(Collectors.toList());
    }

    @Async("viSegmentExecutor")
    private CompletableFuture<List<Segment>> getAllSegmentsByAirportCode(String airportCode, String... keys) {
        redisRepository.segmentValues(Segment.class, airportCode, keys);
        return CompletableFuture.completedFuture(redisRepository.segmentValues(Segment.class, airportCode, keys));
    }

    public List<List<SegmentWithLayover>> buildFilteredSegmentCombinations(
            String origin, String destination, LocalDate departureDate, int pax) throws Exception {

        if (!redisRepository.isRoutePresentInVI("VI", origin + "-" + destination)) {
            log.warn("No VI route found in Redis for {} -> {}", origin, destination);
            return Collections.emptyList();
        }
        log.info("Found VI route for {} -> {}", origin, destination);

        List<String> segmentKeys = redisRepository.fetchSegmentKeysForDates(departureDate);
        Map<String, List<String>> airlineKeyMap = VIUtil.groupByAirline(segmentKeys); // group by airline
        List<RouteLeg> allLegs = VIUtil.buildAllLegs(airlineKeyMap);
        Map<String, List<RouteLeg>> graph = VIUtil.buildgraph(allLegs);

        List<List<RouteLeg>> validPaths = findValidPaths(origin, destination, graph);
        if (validPaths.isEmpty()) {
            log.info("No valid paths found for {} -> {}", origin, destination);
            return Collections.emptyList();
        }

        List<SegmentWithLayover> allSegments = fetchSegmentsFromValidPaths(validPaths);
        List<List<SegmentWithLayover>> finalCombinations = VIUtil.generateItineraries(origin, destination, departureDate, pax, allSegments);
        List<List<SegmentWithLayover>> filteredCombinations = filterValidCombinations(finalCombinations);

        VIUtil.logCombinations(filteredCombinations); // keep or remove based on needs
        return filteredCombinations;
    }

    public List<Flights> convertSegmentCombinationsToFlights(List<List<SegmentWithLayover>> filteredCombinations) {
        return VIUtil.convertToNewFormat(filteredCombinations);
    }

    public List<Flights> fetchVIResult(String dep, String arr, LocalDate date, int pax) {
        String key = dep + "|" + arr + "|" + date;
        List<List<SegmentWithLayover>> itineraries = redisRepository.get(VI_CACHE_INDEX, key);
        List<Flights> result = convertSegmentCombinationsToFlights(itineraries);
        return result;
    }

    private List<List<RouteLeg>> findValidPaths(String origin, String destination, Map<String, List<RouteLeg>> graph) {
        List<List<RouteLeg>> validPaths = new ArrayList<>();
        VIUtil.findValidPaths(graph, origin, destination, validPaths, pathConfig.getMaxLegs());
        log.info("Found {} valid VI path combinations", validPaths.size());
        return validPaths; //build paths
    }

    private List<SegmentWithLayover> fetchSegmentsFromValidPaths(List<List<RouteLeg>> validPaths) {
        List<RouteLeg> uniqueLegs = validPaths.stream()
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());

        List<String> segmentKeys = uniqueLegs.stream()
                .map(leg -> String.join("|",
                        leg.getFrom(),
                        leg.getTo(),
                        leg.getFlightNumber(),
                        leg.getDate(),
                        leg.getAirlineCode()))
                .distinct()
                .collect(Collectors.toList());

        Map<String, List<String>> segmentKeyMap = RouteUtil.generateSegmentKeyMap(segmentKeys.toArray(new String[0]));

        log.info("Fetching {} segment keys for {} airlines",
                segmentKeys.size(),
                segmentKeyMap.keySet().size());

        return viSegmentDetails(segmentKeyMap, SegmentWithLayover::new);
    }


    private List<List<SegmentWithLayover>> filterValidCombinations(List<List<SegmentWithLayover>> combinations) {
        return combinations.stream()
                .filter(itinerary -> {
                    int size = itinerary.size();
                    if (size == 2 || size == 3) {
                        Set<String> airlines = itinerary.stream().map(SegmentWithLayover::getAirline).collect(Collectors.toSet());
                        return airlines.size() > 1;
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }
}
