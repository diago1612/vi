package com.ibs.vi.serviceImpl;

import com.ibs.vi.Exception.RouteNotFoundException;
import com.ibs.vi.model.Airport;
import com.ibs.vi.model.Route;
import com.ibs.vi.repository.RedisRepository;
import com.ibs.vi.service.RouteService;
import com.ibs.vi.util.RouteUtil;
import com.ibs.vi.view.AirportView;
import com.ibs.vi.view.BasicResponseView;
import com.ibs.vi.view.RouteView;
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
    private RedisRepository redisRepository;

    @Override
    public BasicResponseView save(Airport airport) {
        try {
            redisRepository.save(INDEX, airport.getCode(), airport);
            return new BasicResponseView();
        } catch (Exception ex) {
            log.error("AIRPORT_DATA_SAVING_FAILED_TO_REDIS_{}", ex);
            return new BasicResponseView(false);
        }
    }

    @Override
    public AirportView getByKey(String key, String... index) {
        if (!redisRepository.hasKey(INDEX, key))
            throw new RouteNotFoundException("AIRPORT_NOT_FOUND_FOR_"+key);
        return new AirportView(redisRepository.get(INDEX, key));
    }

    @Override
    public List<AirportView> getAll(String... keys) {
        return redisRepository.values(Airport.class, INDEX, keys).stream()
                .map(v -> new AirportView(v))
                .collect(Collectors.toList());
    }

    @Override
    public AirportView updateByKey(String key, Airport airport, String... index) {

        try{
            if (!redisRepository.hasKey(INDEX, key))
                throw new RouteNotFoundException("AIRPORT_NOT_FOUND_FOR_"+key);
            deleteByKey(key);
            redisRepository.put(INDEX, airport.getCode(), airport);
            return new AirportView(airport);
        }catch(RouteNotFoundException ex){
            log.error("AIRPORT_NOT_FOUND_FOR_{}", key, ex);
            throw ex;
        }
        catch(Exception ex){
            log.error("DATA_UPDATE_FAILED_TO_REDIS_FOR_KEY_{}_{}", key, ex);
            throw ex;
        }
    }

    @Override
    public BasicResponseView deleteByKey(String key, String... index) {
        return redisRepository.delete(INDEX, key) ? new BasicResponseView() : new BasicResponseView(false);
    }

    @Override
    public BasicResponseView deleteAll(String... index) {
        return redisRepository.delete(INDEX) ? new BasicResponseView() : new BasicResponseView(false);
    }
}
