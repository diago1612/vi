package com.ibs.vi.serviceImpl;

import com.ibs.vi.model.Airline;
import com.ibs.vi.service.RouteService;
import com.ibs.vi.view.AirlineView;
import com.ibs.vi.view.BasicResponseView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service("airlineManagementService")
public class AirlineServiceImpl implements RouteService<Airline, AirlineView> {

    private static final String INDEX = "AIRLINE_INDEX";

    private static final Logger log = LoggerFactory.getLogger(AirlineServiceImpl.class);


    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public BasicResponseView save(Airline airline) {
        try {
            airline.setValid(true);
            redisTemplate.opsForHash().put(INDEX, airline.getAirlineCode(), airline);

            return new BasicResponseView();
        } catch (Exception ex) {
            log.error("AIRLINE_DATA_SAVING_FAILED_TO_REDIS_{}", ex);
            return new BasicResponseView(false);
        }
    }

    @Override
    public AirlineView getByKey(String key, String... index) {
        Object airline = redisTemplate.opsForHash().get(INDEX, key);
        if (airline == null) {
            throw new RuntimeException("AIRLINE_NOT_FOUND_FOR_" + key);
        }
        return new AirlineView((Airline) airline);
    }

    @Override
    public List<AirlineView> getAll(String... keys) {
        Collection<Object> values = (keys == null || keys.length == 0)
                ? redisTemplate.opsForHash().values(INDEX)
                : redisTemplate.opsForHash().multiGet(INDEX, Arrays.asList(keys));

        return values.stream()
                .filter(value -> value instanceof Airline)
                .map(value -> new AirlineView((Airline) value))
                .collect(Collectors.toList());
    }

    @Override
    public AirlineView updateByKey(String key, Airline airline, String... index) {
        if (!redisTemplate.opsForHash().hasKey(INDEX, key)) {
            throw new RuntimeException("AIRLINE_NOT_FOUND_FOR_" + key);
        }
        deleteByKey(key);
        airline.setValid(true);
        redisTemplate.opsForHash().put(INDEX, airline.getAirlineCode(), airline);
        return new AirlineView(airline);
    }

    @Override
    public BasicResponseView deleteByKey(String key, String... index) {
        Long removed = redisTemplate.opsForHash().delete(INDEX, key);
        return removed != null && removed > 0 ? new BasicResponseView() : new BasicResponseView(false);
    }

    @Override
    public BasicResponseView deleteAll(String... index) {
        redisTemplate.delete(INDEX);
        return new BasicResponseView();
    }
}
