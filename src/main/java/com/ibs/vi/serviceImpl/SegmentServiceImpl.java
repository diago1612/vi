package com.ibs.vi.serviceImpl;

import com.ibs.vi.model.Segment;
import com.ibs.vi.service.RouteService;
import com.ibs.vi.service.VIRouteLogic;
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


@Service("segmentManagementService")
public class SegmentServiceImpl implements RouteService<Segment, SegmentView>, VIRouteLogic {

    @Autowired
    @Qualifier("airlineManagementService")
    private RouteService routeService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final Logger log = LoggerFactory.getLogger(SegmentServiceImpl.class);
    @Override
    public BasicResponseView save(Segment input) {return null;}

    @Override
    public SegmentView getByKey(String key, String... index) {return null;}

    @Override
    public List<SegmentView> getAll(String... keys) {
        List<Segment> segmentList = Optional.ofNullable(viSegmentDetails(Arrays.asList(keys))).orElse(Collections.emptyList());
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
       return null;
    }

    @Override
    public BasicResponseView deleteAll(String... index) {
        redisTemplate.delete(Arrays.asList(index));
        return new BasicResponseView();
    }

    @Override
    public List<Segment> viSegmentDetails(List<String> keys, String... index) {

        List<AirlineView> airlineList = Optional.ofNullable(index == null || index.length == 0 ?
                routeService.getAll() : routeService.getAll(index)).orElse(Collections.emptyList());

        List<String> activeAirlineCode = airlineList.stream()
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
    private CompletableFuture<List<Segment>> getAllSegmentsByAirportCode(List<String> keys, String airportCode){

        Collection<Object> values = (keys == null || keys.size() == 0)
                ? redisTemplate.opsForHash().values(airportCode)
                : redisTemplate.opsForHash().multiGet(airportCode, Arrays.asList(keys));

        List<Segment> segmentList = values.stream()
                .filter(value -> value instanceof Segment)
                .map(s -> (Segment) s)
                .collect(Collectors.toList());
        return CompletableFuture.completedFuture(segmentList);
    }
}
