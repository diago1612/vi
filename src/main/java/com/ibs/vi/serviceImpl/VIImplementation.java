package com.ibs.vi.serviceImpl;

import com.ibs.vi.model.Airline;
import com.ibs.vi.model.RouteLeg;
import com.ibs.vi.model.Segment;
import com.ibs.vi.repository.RedisRepository;
import com.ibs.vi.service.VIService;
import com.ibs.vi.util.VIUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class VIImplementation implements VIService {

    @Autowired
    private RedisRepository redisRepository;

    private static final Logger log = LoggerFactory.getLogger(VIImplementation.class);
    private static final String AIRLINE_INDEX = "AIRLINE";

    @Override
    public List<Segment> viSegmentDetails(String[] keys, String... airlineCodes) {

        List<String> activeAirlineCode = redisRepository.values(Airline.class, AIRLINE_INDEX, airlineCodes)
                .stream()
                .filter(air -> air.isValid())
                .map(air -> air.getAirlineCode())
                .collect(Collectors.toList());

        if (activeAirlineCode.isEmpty()) {
            return Collections.emptyList();
        }

        List<CompletableFuture<List<Segment>>> futures = new ArrayList<>();
        activeAirlineCode.forEach(ac -> futures.add(getAllSegmentsByAirportCode(keys, ac)));

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        List<Segment> segmentList = futures.stream()
                .flatMap(future -> {
                    try {
                        return future.join().stream();
                    } catch (Exception e) {
                        log.error("ERROR_FETCHING_AIRLINE", e);
                        return Stream.empty();  // Skip failed ones
                    }
                })
                .collect(Collectors.toList());

        return segmentList;
    }

    @Async("viSegmentExecutor")
    private CompletableFuture<List<Segment>> getAllSegmentsByAirportCode(String[] keys, String airportCode){
        List<Segment> segmentList = redisRepository.values(Segment.class, airportCode, keys);
        return CompletableFuture.completedFuture(segmentList);
    }

    @Override
    public List<List<Segment>> generateVIItineraries(String origin, String destination, LocalDate departureDate, int pax) throws Exception {
        if (!redisRepository.isRoutePresentInVI("VI", origin + "-" + destination)) {
            log.warn("No VI route found in Redis for {} -> {}", origin, destination);
            return Collections.emptyList();
        }

        log.info("Found VI route for {} -> {}", origin, destination);

        List<String> segmentKeys = redisRepository.fetchSegmentKeysForDates(departureDate);
        Map<String, List<String>> airlineKeyMap = VIUtil.groupByAirline(segmentKeys); //splitting 5 parts into 4 and group by airinecode
        List<RouteLeg> allLegs = VIUtil.buildAllLegs(airlineKeyMap); // remove | from keys and map into object class
        Map<String, List<RouteLeg>> graph = VIUtil.buildgraph(allLegs);

        List<List<RouteLeg>> validPaths = findValidPaths(origin, destination, graph); // build separate paths
        if (validPaths.isEmpty()) {
            log.info("No valid paths found for {} -> {}", origin, destination);
            return Collections.emptyList();
        }

        List<Segment> allSegments = fetchSegmentsFromValidPaths(validPaths); //split path into legs avoid duplicates + create segment keys
        List<List<Segment>> finalCombinations = VIUtil.generateItineraries(origin, destination, departureDate, pax, allSegments); //apply layover, date checks
        List<List<Segment>> filteredCombinations = filterValidCombinations(finalCombinations); //filtering 2/3 stops

        logCombinations(filteredCombinations); //remove later
        return filteredCombinations;
    }

    private List<List<RouteLeg>> findValidPaths(String origin, String destination, Map<String, List<RouteLeg>> graph) {
        List<List<RouteLeg>> validPaths = new ArrayList<>();
        VIUtil.findValidPaths(graph, origin, destination, validPaths);
        log.info("Found {} valid VI path combinations", validPaths.size());
        return validPaths; //build paths
    }

    private List<Segment> fetchSegmentsFromValidPaths(List<List<RouteLeg>> validPaths) {
        List<RouteLeg> uniqueLegs = validPaths.stream()
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());

        List<String> segmentKeys = uniqueLegs.stream()
                .map(leg -> leg.getFrom() + "|" + leg.getTo() + "|" + leg.getFlightNumber() + "|" + leg.getDate())
                .distinct()
                .collect(Collectors.toList());

        Set<String> airlineCodes = uniqueLegs.stream()
                .map(RouteLeg::getAirlineCode)
                .collect(Collectors.toSet()); // to know hashes

        log.info("Fetching {} segments for {} airlines", segmentKeys.size(), airlineCodes.size());
        return viSegmentDetails(segmentKeys.toArray(new String[0]), airlineCodes.toArray(new String[0])); //pick details
    }

    private List<List<Segment>> filterValidCombinations(List<List<Segment>> combinations) {
        return combinations.stream()
                .filter(itinerary -> {
                    int size = itinerary.size();
                    if (size == 2 || size == 3) {
                        Set<String> airlines = itinerary.stream().map(Segment::getAirlineCode).collect(Collectors.toSet());
                        return airlines.size() > 1;
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

    private void logCombinations(List<List<Segment>> combinations) { //can be removed after testing
        log.info("Filtered to {} valid combinations", combinations.size());
        for (int i = 0; i < combinations.size(); i++) {
            List<Segment> itinerary = combinations.get(i);
            String path = itinerary.stream()
                    .map(s -> String.format("%s(%s:%s→%s)",
                            s.getFlightNumber(), s.getAirlineCode(),
                            s.getDepartureAirportCode(), s.getArrivalAirportCode()))
                    .collect(Collectors.joining(" -> "));
            log.info("Itinerary {}: {}", i + 1, path);
        }
    }

}
