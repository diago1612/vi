package com.ibs.vi.serviceImpl;

import com.ibs.vi.model.PathConfig;
import com.ibs.vi.repository.RedisRepository;
import com.ibs.vi.scheduler.DynamicVIScheduler;
import com.ibs.vi.view.BasicResponseView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VIConfigService {

    private static final String INDEX = "CONFIGURATION";

    @Autowired
    private RedisRepository redisRepository;

    @Autowired
    private PathConfig pathConfig;

    @Autowired
    private DynamicVIScheduler dynamicScheduler;

    public BasicResponseView getMaxLegs() {
        try {
            String redisVal = redisRepository.get(INDEX, "maxLegs");
            int value = (redisVal != null) ? Integer.parseInt(redisVal) : pathConfig.getMaxLegs();
            return new BasicResponseView(value);
        } catch (Exception e) {
            return new BasicResponseView("Error retrieving max legs: " + e.getMessage());
        }
    }

    public BasicResponseView setMaxLegs(int value) {
        if (value < 1) {
            return new BasicResponseView("Value must be >= 1");
        }

        try {
            redisRepository.save(INDEX, "maxLegs", String.valueOf(value));
            return new BasicResponseView("Max legs updated to " + value);
        } catch (Exception e) {
            return new BasicResponseView("Failed to update max legs: " + e.getMessage());
        }
    }

    public BasicResponseView getDepartureWindowDays() {
        try {
            String redisVal = redisRepository.get(INDEX, "departureWindowDays");
            int value = (redisVal != null) ? Integer.parseInt(redisVal) : pathConfig.getDepartureWindowDays();
            return new BasicResponseView(value);
        } catch (Exception e) {
            return new BasicResponseView("Error retrieving departure window: " + e.getMessage());
        }
    }

    public BasicResponseView setDepartureWindowDays(int value) {
        if (value < 0) {
            return new BasicResponseView("Value must be >= 0");
        }

        try {
            redisRepository.save(INDEX, "departureWindowDays", String.valueOf(value));
            return new BasicResponseView("Departure window days updated to " + value);
        } catch (Exception e) {
            return new BasicResponseView("Failed to update departure window: " + e.getMessage());
        }
    }

    public BasicResponseView updateInterval(long minutes) {
        if (minutes < 1) {
            return new BasicResponseView("Interval must be at least 1 minute");
        }

        dynamicScheduler.scheduleTask(minutes);
        return new BasicResponseView("Updated VI scheduler to run every " + minutes + " minutes");
    }

    public BasicResponseView getCurrentInterval() {
        return new BasicResponseView("Current interval: " + dynamicScheduler.getCurrentInterval() + " minutes");
    }
}
