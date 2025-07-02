package com.ibs.vi.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibs.vi.model.Airline;
import com.ibs.vi.model.Airport;
import com.ibs.vi.model.Segment;
import com.ibs.vi.repository.RedisRepository;
import com.ibs.vi.service.RouteService;
import com.ibs.vi.service.VIService;
import com.ibs.vi.util.RouteUtil;
import com.ibs.vi.view.BasicResponseView;
import com.ibs.vi.view.SegmentView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service("segmentManagementService")
public class SegmentServiceImpl implements RouteService<Segment, SegmentView> {

    @Autowired
    private RedisRepository redisRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VIService viService;

    private static final Logger log = LoggerFactory.getLogger(SegmentServiceImpl.class);
    private static final String AIRLINE_INDEX = "AIRLINE";
    private static final String AIRPORT_INDEX = "AIRPORT";

    @Override
    public BasicResponseView save(Segment segment) {
        try {

            //Save Airline if not present
            Airline airline = Airline.fromSegment(segment);
            if (!redisRepository.hasKey(AIRLINE_INDEX, airline.getAirlineCode())) {
                redisRepository.save(AIRLINE_INDEX, airline.getAirlineCode(), airline);
                log.info("Saved to AIRLINE hash - HashKey: {}, AirlineKey: {}", AIRLINE_INDEX, airline.getAirlineCode());
            } else {
                log.info("Airline already exists in Redis - Skipping save for AirlineKey: {}", airline.getAirlineCode());
            }

            //Save Airport if not present
            Airport departureAirport = Airport.fromDepartureSegment(segment);
            if (!redisRepository.hasKey(AIRPORT_INDEX, departureAirport.getCode())) {
                redisRepository.save(AIRPORT_INDEX, departureAirport.getCode(), departureAirport);
                log.info("Saved to AIRPORT hash - HashKey: {}, AirportKey: {}", AIRPORT_INDEX, departureAirport.getCode());
            } else {
                log.info("Airport already exists in Redis - Skipping save for AirportKey: {}", departureAirport.getCode());
            }

            Airport arrivalAirport = Airport.fromArrivalSegment(segment);
            if (!redisRepository.hasKey(AIRPORT_INDEX, arrivalAirport.getCode())) {
                redisRepository.save(AIRPORT_INDEX, arrivalAirport.getCode(), arrivalAirport);
                log.info("Saved to AIRPORT hash - HashKey: {}, AirportKey: {}", AIRPORT_INDEX, arrivalAirport.getCode());
            } else {
                log.info("Airport already exists in Redis - Skipping save for AirportKey: {}", arrivalAirport.getCode());
            }

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
        List<Segment> segmentList = Optional.ofNullable(viService.viSegmentDetails(keys)).orElse(Collections.emptyList());
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

}
