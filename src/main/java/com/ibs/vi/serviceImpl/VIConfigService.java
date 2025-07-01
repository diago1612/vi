package com.ibs.vi.serviceImpl;

import com.ibs.vi.model.PathConfig;
import com.ibs.vi.repository.RedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VIConfigService {

    private static final String CONFIG_HASH_KEY = "vi:config";

    @Autowired
    private RedisRepository redisRepository;

    @Autowired
    private PathConfig pathConfig;

    public int getMaxLegs() {
        String redisVal = redisRepository.get(CONFIG_HASH_KEY, "maxLegs");
        try {
            return (redisVal != null) ? Integer.parseInt(redisVal) : pathConfig.getMaxLegs();
        } catch (NumberFormatException e) {
            return pathConfig.getMaxLegs();
        }
    }

    public int getDepartureWindowDays() {
        String redisVal = redisRepository.get(CONFIG_HASH_KEY, "departureWindowDays");
        try {
            return (redisVal != null) ? Integer.parseInt(redisVal) : pathConfig.getDepartureWindowDays();
        } catch (NumberFormatException e) {
            return pathConfig.getDepartureWindowDays();
        }
    }

    public void updateMaxLegs(int value) throws Exception {
        redisRepository.save(CONFIG_HASH_KEY, "maxLegs", String.valueOf(value));
    }

    public void updateDepartureWindowDays(int value) throws Exception {
        redisRepository.save(CONFIG_HASH_KEY, "departureWindowDays", String.valueOf(value));
    }
}

