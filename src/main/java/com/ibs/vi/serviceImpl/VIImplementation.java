package com.ibs.vi.serviceImpl;

import com.ibs.vi.model.RouteLeg;
import com.ibs.vi.model.Segment;
import com.ibs.vi.repository.RedisRepository;
import com.ibs.vi.service.VIService;
import com.ibs.vi.util.VIUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VIImplementation implements VIService {

    @Autowired
    private RedisRepository redisRepository;

    private static final Logger log = LoggerFactory.getLogger(VIImplementation.class);

    @Override
    public List<Segment> generateVIItineraries(String origin, String destination, LocalDate departureDate, int pax) throws Exception {
        String key = "VI"; // hash name
        String route = origin + "-" + destination;

        if (!redisRepository.isRoutePresentInVI(key, route)) {
            log.warn("No VI route found in Redis for {} -> {}", origin, destination);
            return Collections.emptyList();
        }

        log.info("Found VI route for {} -> {}", origin, destination);

        List<String> segmentKeys = redisRepository.fetchSegmentKeysForDates(departureDate);

        log.info(
                "Fetched {} segment keys from Redis SortedSet 'SortedSegmentKeys' between {} and {}: {}",
                segmentKeys.size(),
                departureDate,
                departureDate.plusDays(2),
                segmentKeys
        );

        //Group segmentKeys by airline
        Map<String, List<String>> airlineKeyMap = VIUtil.groupByAirline(segmentKeys);
        log.info("Grouped keys by airline: {}", airlineKeyMap);

        //Build all legs from keys
        List<RouteLeg> allLegs = VIUtil.buildAllLegs(airlineKeyMap);
        log.info("Built {} RouteLegs from airline-key map", allLegs.size());

        //Build graph from all legs
        Map<String, List<RouteLeg>> graph = VIUtil.buildGraph(allLegs);
        log.info("Graph built with {} airports", graph.size());

        //Find valid paths from source to destination
        List<List<RouteLeg>> validPaths = new ArrayList<>();
        VIUtil.findValidPaths(graph, origin, destination, validPaths);
        log.info("Found {} valid VI path combinations", validPaths.size());
        List<RouteLeg> validLegs = validPaths.stream()
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());
        log.info("Total unique legs involved: {}", validLegs.size());

        int count = 1;
        for (List<RouteLeg> path : validPaths) {
            log.info("Combination #{}:", count++);
            for (RouteLeg leg : path) {
                log.info("  {} -> {} on {} via {}", leg.getFrom(), leg.getTo(), leg.getDate(), leg.getAirlineCode());
            }
        }

        log.info("VI itinerary generation complete for {} -> {}", origin, destination);
        //TODO : Fetch values from redis key
        return Collections.emptyList();
    }

}
