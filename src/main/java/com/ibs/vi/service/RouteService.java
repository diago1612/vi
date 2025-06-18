
package com.ibs.vi.service;

import com.ibs.vi.view.BasicResponseView;

import java.util.List;

/**
 *
 * @author jithin123
 */
public interface RouteService<T, V> {

    BasicResponseView save(T input);
    V getByKey(String key);
    List<V> getAll();
    V updateByKey(String key, T input);
    BasicResponseView deleteByKey(String key);
    BasicResponseView deleteAll();
    
}
