
package com.ibs.vi.service;

import java.util.Map;

/**
 *
 * @author jithin123
 */
public interface RouteService<T> {
    
    void save(T input);
    T getByKey(String key);
    Map<String,T> getAll();
    T update(String key);
    void deleteByKey(String key);
    
}
