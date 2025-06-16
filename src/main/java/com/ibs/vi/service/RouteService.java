
package com.ibs.vi.service;

import com.ibs.vi.view.BasicResponseView;

import java.util.Map;

/**
 *
 * @author jithin123
 */
public interface RouteService<T> {

    BasicResponseView save(T input);
    T getByKey(String key);
    Map<String,T> getAll();
    T update(T input);
    BasicResponseView deleteByKey(String key);
    BasicResponseView delete();
    
}
