package com.ibs.vi.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibs.vi.model.Airline;
import com.ibs.vi.model.Segment;
import com.ibs.vi.repository.RedisRepository;
import com.ibs.vi.service.RouteService;
import com.ibs.vi.service.VIRouteLogic;
import com.ibs.vi.util.RouteUtil;
import com.ibs.vi.view.BasicResponseView;
import com.ibs.vi.view.SegmentView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service("segmentManagementService")
public class SegmentServiceImpl implements RouteService<Segment, SegmentView>, VIRouteLogic {
    private static final String AIRLINE_INDEX = "AIRLINE_INDEX";

    @Autowired
    private RedisRepository redisRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static final Logger log = LoggerFactory.getLogger(SegmentServiceImpl.class);
    @Override
    public BasicResponseView save(Segment segment) {
        try {
            //Save to Hash
            String airlineHash = segment.airlineCode;
            String segmentKey = RouteUtil.generateSegmentKey(segment);
            redisRepository.save(airlineHash, segmentKey, segment);

            log.info("Saved to Redis Hash - HashKey: {}, SegmentKey: {}", airlineHash, segmentKey);
            // Save to ZSET
            String sortedSetKey = "SortedSegmentKeys";
            String sortedKey = RouteUtil.generateSortedSegmentKey(segment);
            double score = RouteUtil.parseDepartureTimeToEpoch(segment.getDepartureTime());
            redisRepository.addToSortedSet(sortedSetKey, sortedKey, score);
            log.info("Saved to Redis SortedSet - SortedSetKey: {}, MemberKey: {}, Score: {}", sortedSetKey, sortedKey, score);

            return new BasicResponseView("Segment saved to hash and sorted set");

        } catch (Exception ex) {
            log.error("DATA_SAVING_FAILED_TO_REDIS_{}", ex.getMessage(), ex);
            return new BasicResponseView("Failed to save segment");
        }
    }


    @Override
    public SegmentView getByKey(String key, String... index) {
        if (index == null || index.length == 0) return null;

        String airlineHash = index[0];
        log.info("Fetching from Redis - hash: {}, key: {}", airlineHash, key);

        Segment segment = redisRepository.get(airlineHash, key);

        if (segment == null) {
            log.warn("No data found for hash: {}, key: {}", airlineHash, key);
            return null;
        }

        log.info("Retrieved segment: {}", segment);
        return new SegmentView(segment);
    }

    @Override
    public List<SegmentView> getAll(String... keys) {
        List<Segment> segmentList = Optional.ofNullable(viSegmentDetails(keys)).orElse(Collections.emptyList());
        return segmentList.stream()
                .map(s -> new SegmentView(s))
                .collect(Collectors.toList());
    }

    @Override
    public SegmentView updateByKey(String key, Segment input, String... index) {
        return null;
    }

    @Override
    public BasicResponseView deleteByKey(String key, String... index) {
        if (index == null || index.length == 0) {
            return new BasicResponseView("Airline not specified");
        }

        String airlineHash = index[0];
        boolean exists = redisRepository.hasKey(airlineHash, key);

        if (exists) {
            redisRepository.delete(airlineHash, key);
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
        boolean deleted = redisRepository.deleteByHashKeys(airlineHash);

        if (deleted) {
            return new BasicResponseView("Deleted all segments for airlineHash: " + airlineHash);
        } else {
            return new BasicResponseView("AirlineHash: " + airlineHash + " does not exist or is already empty");
        }
    }


    @Override
    public List<Segment> viSegmentDetails(String[] keys, String... airportCodes) {

        List<String> activeAirlineCode = redisRepository.values(Airline.class, AIRLINE_INDEX, airportCodes)
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
}
