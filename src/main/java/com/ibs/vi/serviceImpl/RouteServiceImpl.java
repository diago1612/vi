
package com.ibs.vi.serviceImpl;

import com.ibs.vi.Exception.RouteNotFoundException;
import com.ibs.vi.model.Route;
import com.ibs.vi.service.RouteService;

import java.util.List;
import java.util.stream.Collectors;

import com.ibs.vi.util.RouteUtil;
import com.ibs.vi.view.BasicResponseView;
import com.ibs.vi.view.RouteView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 *
 * @author jithin123
 */
@Service
public class RouteServiceImpl implements RouteService<Route, RouteView>{
    
    private static final String INDEX = "VI";
    private static final Logger log = LoggerFactory.getLogger(RouteServiceImpl.class);
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public BasicResponseView save(Route route) {
        try{
            redisTemplate.opsForHash().put(INDEX, RouteUtil.generateKey(route), route);
            return new BasicResponseView();
        }catch(Exception ex){
            log.error("DATA_SAVING_FAILED_TO_REDIS_{}", ex);
            return new BasicResponseView(false);
        }

    }

    @Override
    public RouteView getByKey(String key) {
        if (!redisTemplate.opsForHash().hasKey(INDEX, key))
            throw new RouteNotFoundException("ROUTE_NOT_FOUND_FOR_"+key);
        return new RouteView((Route) redisTemplate.opsForHash().get(INDEX, key));
    }

    @Override
    public List<RouteView> getAll() {
        return redisTemplate.opsForHash().values(INDEX).stream()
                .filter(value -> value instanceof Route)
                .map(v -> new RouteView((Route) v))
                .collect(Collectors.toList());
    }

    @Override
    public RouteView updateByKey(String key, Route route) {
        if (!redisTemplate.opsForHash().hasKey(INDEX, key))
            throw new RouteNotFoundException("ROUTE_NOT_FOUND_FOR_"+key);
        deleteByKey(key);
        redisTemplate.opsForHash().put(INDEX, RouteUtil.generateKey(route), route);
        return new RouteView(route);
    }

    @Override
    public BasicResponseView deleteByKey(String key) {
        Long removed = redisTemplate.opsForHash().delete(INDEX, key);
        if (removed != null && removed > 0)
            return new BasicResponseView();
        log.warn("NO_DATA_FOUND_FOR_{}", key);
        return new BasicResponseView(false);
    }

    @Override
    public BasicResponseView deleteAll() {
        redisTemplate.delete(INDEX);
        return new BasicResponseView();
    }

}
