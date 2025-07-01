package com.ibs.vi.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "vi")
public class PathConfig {
    private int maxLegs;
    private int departureWindowDays;

    public int getMaxLegs() {
        return maxLegs;
    }

    public void setMaxLegs(int maxLegs) {
        this.maxLegs = maxLegs;
    }

    public int getDepartureWindowDays() {
        return departureWindowDays;
    }

    public void setDepartureWindowDays(int departureWindowDays) {
        this.departureWindowDays = departureWindowDays;
    }
}

