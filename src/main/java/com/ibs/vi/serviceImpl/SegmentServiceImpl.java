package com.ibs.vi.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibs.vi.model.Segment;
import com.ibs.vi.service.RouteService;
import com.ibs.vi.util.RouteUtil;
import com.ibs.vi.view.AirlineView;
import com.ibs.vi.view.BasicResponseView;
import com.ibs.vi.view.SegmentView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service("segmentManagementService")
public class SegmentServiceImpl implements RouteService<Segment, SegmentView> {

    @Autowired
    @Qualifier("airlineManagementService")
    private RouteService routeService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static final Logger log = LoggerFactory.getLogger(SegmentServiceImpl.class);
    @Override
    public BasicResponseView save(Segment segment) {
        if (segment == null || segment.getAirline() == null) {
            return new BasicResponseView("Invalid Segment");
        }

        String airline = segment.getAirline();
        String flight = segment.getFlightNumber();
        String key = RouteUtil.generateKeyForSegments(segment);
        String hashName = airline;
        String zaddMember = key + "|" + airline;

        // Fetch existing list from airline-specific hash
        List<Segment> existingSegments =
                (List<Segment>) redisTemplate.opsForHash().get(hashName, key);

        if (existingSegments == null) {
            existingSegments = new ArrayList<>();
        }
        existingSegments.add(segment);

        try {
            log.info("Saving to Redis - hash: {}, key: {}, segments: {}",
                    hashName, key, objectMapper.writeValueAsString(existingSegments));
        } catch (JsonProcessingException e) {
            log.error("Failed to log segments as JSON", e);
        }

        // Save to hash
        redisTemplate.opsForHash().put(hashName, key, existingSegments);

        // Convert date from key to epoch seconds
        try {
            String[] parts = key.split("\\|");
            String dateStr = parts[2];
            long epochSeconds = LocalDate.parse(dateStr)
                    .atStartOfDay(ZoneOffset.UTC)
                    .toEpochSecond();

            // Add to sorted set (ZADD)
            redisTemplate.opsForZSet().add("SortedSegmentKeys", zaddMember, epochSeconds);
            log.info("ZADD -> set: {}, member: {}, score(epoch): {}", "AllSegmentKeys", zaddMember, epochSeconds);
        } catch (Exception e) {
            log.error("Failed to parse date and save to ZADD", e);
        }
        return new BasicResponseView("Segment Saved");
    }


    @Override
    public SegmentView getByKey(String key, String... index) {
        if (index == null || index.length == 0) return null;
        String airlineHash = index[0];
        log.info("Fetching from Redis - hash: {}, key: {}", airlineHash, key);
        List<Segment> segments = (List<Segment>) redisTemplate.opsForHash().get(airlineHash, key);

        if (segments == null) {
            log.warn("No data found for hash: {}, key: {}", airlineHash, key);
            return null;
        }

        log.info("Retrieved segments: {}", segments);
        return segments.isEmpty() ? null : new SegmentView(segments.get(0));
    }

    @Override
    public List<SegmentView> getAll(String... keys) {
        return getSegmentDetails(keys);
    }

    @Override
    public SegmentView updateByKey(String key, Segment updateSegment, String... index) {
        if (index == null || index.length == 0 || updateSegment == null) return null;

        String airlineHash = index[0];
        List<Segment> segments = (List<Segment>) redisTemplate.opsForHash().get(airlineHash, key);

        if (segments == null) return null;

        for (int i = 0; i < segments.size(); i++) {
            Segment s = segments.get(i);
            if (s.getFlightNumber().equals(updateSegment.getFlightNumber())) {
                segments.set(i, updateSegment);
                redisTemplate.opsForHash().put(airlineHash, key, segments);
                return new SegmentView(updateSegment);
            }
        }
        return null;
    }

    @Override
    public BasicResponseView deleteByKey(String key, String... index) {
        if (index == null || index.length == 0) {
            return new BasicResponseView("Airline not specified");
        }

        String airlineHash = index[0];
        Boolean hasKey = redisTemplate.opsForHash().hasKey(airlineHash, key);

        if (Boolean.TRUE.equals(hasKey)) {
            redisTemplate.opsForHash().delete(airlineHash, key);
            return new BasicResponseView("Deleted key: " + key + " from airlineHash: " + airlineHash);
        } else {
            return new BasicResponseView("Key: " + key + " not found in airlineHash: " + airlineHash);
        }
    }


    @Override
    public BasicResponseView deleteAll(String... index) {
        if (index == null || index.length == 0) {
            return new BasicResponseView("Airline not specified");
        }

        String airlineHash = index[0];
        Boolean exists = redisTemplate.hasKey(airlineHash);

        if (Boolean.TRUE.equals(exists)) {
            redisTemplate.delete(airlineHash);
            return new BasicResponseView("Deleted all segments for airlineHash: " + airlineHash);
        } else {
            return new BasicResponseView("AirlineHash: " + airlineHash + " does not exist");
        }
    }


    private List<SegmentView>getSegmentDetails(String[] keys){
        List<AirlineView> airlineList = Optional.ofNullable(routeService.getAll()).orElse(Collections.emptyList());
        List<String> activeAirlineCode = airlineList.stream()
                .filter(air -> air.isValid())
                .map(air -> air.getKey())
                .collect(Collectors.toList());

        if (activeAirlineCode.isEmpty()) {
            return Collections.emptyList();
        }

        List<CompletableFuture<List<SegmentView>>> futures = new ArrayList<>();
        activeAirlineCode.forEach(ac -> futures.add(getAllSegmentsByAirportCode(keys, ac)));

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        List<SegmentView> segmentViewList = futures.stream()
                .flatMap(future -> {
                    try {
                        return future.join().stream();
                    } catch (Exception e) {
                        log.error("ERROR_FETCHING_AIRLINE", e);
                        return Stream.empty();  // Skip failed ones
                    }
                })
                .collect(Collectors.toList());

        return segmentViewList;
    }

    @Async("viSegmentExecutor")
    private CompletableFuture<List<SegmentView>> getAllSegmentsByAirportCode(String[] keys, String airportCode){

        Collection<Object> values = (keys == null || keys.length == 0)
                ? redisTemplate.opsForHash().values(airportCode)
                : redisTemplate.opsForHash().multiGet(airportCode, Arrays.asList(keys));

        List<SegmentView> segmentViewList = values.stream()
                .filter(value -> value instanceof Segment)
                .map(s -> new SegmentView((Segment) s))
                .collect(Collectors.toList());
        return CompletableFuture.completedFuture(segmentViewList);
    }
}
