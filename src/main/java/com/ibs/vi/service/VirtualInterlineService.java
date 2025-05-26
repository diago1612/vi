package com.ibs.vi.service;

import com.ibs.vi.model.VirtualInterlineDataModel;
import java.util.List;


public interface VirtualInterlineService {

    String getHealthStatus();
    
    public List<List<VirtualInterlineDataModel>> fetchFlightDetails(String source, String destination, String startDate) throws Exception;
}
