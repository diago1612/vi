
package com.ibs.vi.service;

import com.ibs.vi.view.BasicResponseView;

import java.util.List;

/**
 *
 * @author jithin123
 */
public interface RouteService<T, V> {

    BasicResponseView save(T input);
    V getByKey(String key, String... index);
    List<V> getAll(String... keys);
    V updateByKey(String key, T input, String... index);
    BasicResponseView deleteByKey(String key, String... index);
    BasicResponseView deleteAll(String... index);
    
}
