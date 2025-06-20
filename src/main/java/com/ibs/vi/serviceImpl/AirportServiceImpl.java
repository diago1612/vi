package com.ibs.vi.serviceImpl;

import com.ibs.vi.model.Airline;
import com.ibs.vi.model.Airport;
import com.ibs.vi.service.RouteService;
import com.ibs.vi.util.AirlineUtil;
import com.ibs.vi.view.AirlineView;
import com.ibs.vi.view.AirportView;
import com.ibs.vi.view.BasicResponseView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service("airportManagementService")
public class AirportServiceImpl implements RouteService<Airport, AirportView> {

    private static final String INDEX = "AIRPORT";

    private static final Logger log = LoggerFactory.getLogger(AirportServiceImpl.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public BasicResponseView save(Airport airport) {
        try {
            redisTemplate.opsForHash().put(INDEX, airport.getCode(), airport);

            return new BasicResponseView();
        } catch (Exception ex) {
            log.error("AIRPORT_DATA_SAVING_FAILED_TO_REDIS_{}", ex);
            return new BasicResponseView(false);
        }
    }

    @Override
    public AirportView getByKey(String key) {
        Object airport = redisTemplate.opsForHash().get(INDEX, key);
        if (airport == null) {
            throw new RuntimeException("AIRPORT_NOT_FOUND_FOR_" + key);
        }
        return new AirportView((Airport) airport);
    }

    @Override
    public List<AirportView> getAll() {
        return redisTemplate.opsForHash().values(INDEX).stream()
                .filter(value -> value instanceof Airport)
                .map(value -> new AirportView((Airport) value))
                .collect(Collectors.toList());
    }

    @Override
    public AirportView updateByKey(String key, Airport airport) {
        if (!redisTemplate.opsForHash().hasKey(INDEX, key)) {
            throw new RuntimeException("AIRPORT_NOT_FOUND_FOR_" + key);
        }
        deleteByKey(key);
        redisTemplate.opsForHash().put(INDEX, airport.getCode(), airport);
        return new AirportView(airport);
    }

    @Override
    public BasicResponseView deleteByKey(String key) {
        Long removed = redisTemplate.opsForHash().delete(INDEX, key);
        return removed != null && removed > 0 ? new BasicResponseView() : new BasicResponseView(false);
    }

    @Override
    public BasicResponseView deleteAll() {
        redisTemplate.delete(INDEX);
        return new BasicResponseView();
    }
}
