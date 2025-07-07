
package com.ibs.vi.serviceImpl;

import com.ibs.vi.exception.RouteNotFoundException;
import com.ibs.vi.model.Route;
import com.ibs.vi.repository.RedisRepository;
import com.ibs.vi.service.RouteService;

import java.util.List;
import java.util.stream.Collectors;

import com.ibs.vi.util.RouteUtil;
import com.ibs.vi.view.BasicResponseView;
import com.ibs.vi.view.RouteView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author jithin123
 */
@Service("routeManagementService")
public class RouteServiceImpl implements RouteService<Route, RouteView>{
    
    private static final String INDEX = "VI";
    private static final Logger log = LoggerFactory.getLogger(RouteServiceImpl.class);
    @Autowired
    private RedisRepository redisRepository;

    @Override
    public BasicResponseView save(Route route) {
        try{
            redisRepository.save(INDEX, RouteUtil.generateKey(route), route);
            return new BasicResponseView();
        }catch(Exception ex){
            log.error("DATA_SAVING_FAILED_TO_REDIS_{}", ex);
            return new BasicResponseView(false);
        }

    }

    @Override
    public RouteView getByKey(String key, String... index) {
        if (!redisRepository.hasKey(INDEX, key))
            throw new RouteNotFoundException("ROUTE_NOT_FOUND_FOR_"+key);
        return new RouteView(redisRepository.get(INDEX, key));
    }

    @Override
    public List<RouteView> getAll(String... keys) {
        return redisRepository.values(Route.class, INDEX, keys).stream()
                .map(v -> new RouteView(v))
                .collect(Collectors.toList());
    }

    @Override
    public RouteView updateByKey(String key, Route route, String... index) {
        try{
            if (!redisRepository.hasKey(INDEX, key))
                throw new RouteNotFoundException("ROUTE_NOT_FOUND_FOR_"+key);
            deleteByKey(key);
            redisRepository.put(INDEX, RouteUtil.generateKey(route), route);
            return new RouteView(route);
        }catch(RouteNotFoundException ex){
            log.error("ROUTE_NOT_FOUND_FOR_{}", key, ex);
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
