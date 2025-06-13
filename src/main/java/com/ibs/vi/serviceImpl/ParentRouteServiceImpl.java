
package com.ibs.vi.serviceImpl;

import com.ibs.vi.model.Route;
import com.ibs.vi.service.RouteService;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

/**
 *
 * @author jithin123
 */
public class ParentRouteServiceImpl implements RouteService<Route>{
    
    private static final String INDEX = "VI";
    private static final String DELIMITTER = "-";
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void save(Route route) {
        StringBuilder key = new StringBuilder();
        key.append(route.getDepartureAirport());
        key.append(DELIMITTER);
        key.append(route.getArrivalAirport());
        
        redisTemplate.opsForHash().put(INDEX, key, route);
    }

    @Override
    public Route getByKey(String key) {
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
    public Route update(String key) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void deleteByKey(String key) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
