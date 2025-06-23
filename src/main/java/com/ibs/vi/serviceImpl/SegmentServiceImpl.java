package com.ibs.vi.serviceImpl;

import com.ibs.vi.model.Segment;
import com.ibs.vi.service.RouteService;
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

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class SegmentServiceImpl implements RouteService<Segment, SegmentView> {

    @Autowired
    @Qualifier("airlineManagementService")
    private RouteService routeService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final Logger log = LoggerFactory.getLogger(SegmentServiceImpl.class);
    @Override
    public BasicResponseView save(Segment input) {
        return null;
    }

    @Override
    public SegmentView getByKey(String key, String... index) {
        return null;
    }

    @Override
    public List<SegmentView> getAll(String... keys) {
        return getSegmentDetails(keys);
    }

    @Override
    public SegmentView updateByKey(String key, Segment input, String... index) {
        return null;
    }

    @Override
    public BasicResponseView deleteByKey(String key, String... index) {
        return null;
    }

    @Override
    public BasicResponseView deleteAll(String... index) {
        return null;
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
