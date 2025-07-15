package com.ibs.vi.model;

public class Airline {

    private String airlineCode;

    private String airlineName;

    private boolean isValid;

    public Airline(){}

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

    public static Airline fromSegment(Segment segment) {
        Airline airline = new Airline();
        airline.setAirlineCode(segment.getAirline());
        airline.setAirlineName(segment.getAirline()); // need to change
        airline.setValid(true);
        return airline;
    }

}


