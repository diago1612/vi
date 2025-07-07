package com.ibs.vi.serviceImpl;

import com.ibs.vi.exception.RouteNotFoundException;
import com.ibs.vi.model.Airline;
import com.ibs.vi.repository.RedisRepository;
import com.ibs.vi.service.RouteService;
import com.ibs.vi.view.AirlineView;
import com.ibs.vi.view.BasicResponseView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service("airlineManagementService")
public class AirlineServiceImpl implements RouteService<Airline, AirlineView> {

    private static final String INDEX = "AIRLINE";

    private static final Logger log = LoggerFactory.getLogger(AirlineServiceImpl.class);

    @Autowired
    private RedisRepository redisRepository;

    @Override
    public BasicResponseView save(Airline airline) {
        try {
            airline.setValid(true);
            redisRepository.save(INDEX, airline.getAirlineCode(), airline);
            return new BasicResponseView();
        } catch (Exception ex) {
            log.error("AIRLINE_DATA_SAVING_FAILED_TO_REDIS_{}", ex);
            return new BasicResponseView(false);
        }
    }

    @Override
    public AirlineView getByKey(String key, String... index) {
        if (!redisRepository.hasKey(INDEX, key))
            throw new RouteNotFoundException("AIRLINE_NOT_FOUND_FOR_"+key);
        return new AirlineView(redisRepository.get(INDEX, key));
    }

    @Override
    public List<AirlineView> getAll(String... keys) {
        return redisRepository.values(Airline.class, INDEX, keys).stream()
                .map(v -> new AirlineView(v))
                .collect(Collectors.toList());
    }

    @Override
    public AirlineView updateByKey(String key, Airline airline, String... index) {
        try{
            if (!redisRepository.hasKey(INDEX, key)) {
                throw new RuntimeException("AIRLINE_NOT_FOUND_FOR_" + key);
            }
            deleteByKey(key);
            airline.setValid(true);
            redisRepository.put(INDEX, airline.getAirlineCode(), airline);
            return new AirlineView(airline);
        }catch(RouteNotFoundException ex){
            log.error("AIRLINE_NOT_FOUND_FOR_{}", key, ex);
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
