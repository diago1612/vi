package com.ibs.vi.serviceImpl;

import com.ibs.vi.service.VirtualInterlineService;
import org.springframework.stereotype.Service;

@Service
public class VirtualInterlineImplementation implements VirtualInterlineService {

    @Override
    public String getHealthStatus() {
        return "Application is healthy";
    }
}
