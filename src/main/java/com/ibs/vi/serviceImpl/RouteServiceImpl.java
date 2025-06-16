
package com.ibs.vi.serviceImpl;

import com.ibs.vi.Exception.RouteNotFoundException;
import com.ibs.vi.model.Route;
import com.ibs.vi.service.RouteService;
import java.util.HashMap;
import java.util.Map;

import com.ibs.vi.util.RouteUtil;
import com.ibs.vi.view.BasicResponseView;
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
public class RouteServiceImpl implements RouteService<Route>{
    
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
    public Route getByKey(String key) {
        if (!redisTemplate.opsForHash().hasKey(INDEX, key))
            throw new RouteNotFoundException("ROUTE_NOT_FOUND_FOR_"+key);
        return (Route) redisTemplate.opsForHash().get(INDEX, key);
    }

    @Override
    public Map<String, Route> getAll() {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(INDEX);
        Map<String, Route> routeMap = new HashMap<>();

    for (Map.Entry<Object, Object> entry : entries.entrySet()) {
        Object key = entry.getKey();
        Object value = entry.getValue();

        if (key instanceof String && value instanceof Route) {
            routeMap.put((String) key, (Route) value);
        }
    }
    return routeMap;
    }

    @Override
    public Route update(Route route) {
        String key = RouteUtil.generateKey(route);
        if (!redisTemplate.opsForHash().hasKey(INDEX, key))
            throw new RouteNotFoundException("ROUTE_NOT_FOUND_FOR_"+key);
        redisTemplate.opsForHash().put(INDEX, key, route);
        return route;
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
    public BasicResponseView delete() {
        redisTemplate.delete(INDEX);
        return new BasicResponseView();
    }

}
