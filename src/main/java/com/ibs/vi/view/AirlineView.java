package com.ibs.vi.view;

import com.ibs.vi.model.Airline;

public class AirlineView {

    public String key;
    private String airlineCode;
    private String airlineName;
    private boolean isValid;

    public AirlineView(Airline airline) {
        this.key = airline.getAirlineCode();
        this.airlineCode = airline.getAirlineCode();
        this.airlineName = airline.getAirlineName();
        this.isValid = airline.isValid();
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

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
