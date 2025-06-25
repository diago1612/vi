package com.ibs.vi.view;

import com.ibs.vi.model.Airline;

public class AirlineView {

    private String airlineCode;

    private String airlineName;
    private boolean isValid;

    public AirlineView(Airline airline) {
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
}
