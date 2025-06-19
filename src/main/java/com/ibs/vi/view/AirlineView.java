package com.ibs.vi.view;

import com.ibs.vi.model.Airline;
import com.ibs.vi.util.AirlineUtil;

public class AirlineView {

    private String key;

    private String airlineCode;

    private String airlineName;

    public AirlineView(Airline airline) {
        this.key = AirlineUtil.generateKey(airline);
        this.airlineCode = airline.getAirlineCode();
        this.airlineName = airline.getAirlineName();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAirlineCode() {
        return airlineCode;
    }

    public void setAirlineCode(String airlineCode) {
        this.airlineCode = airlineCode;
    }

    public String getAirlineName() {
        return airlineName;
    }

    public void setAirlineName(String airlineName) {
        this.airlineName = airlineName;
    }
}
