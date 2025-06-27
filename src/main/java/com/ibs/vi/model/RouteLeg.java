package com.ibs.vi.model;

public class RouteLeg {
    private String from;
    private String to;
    private String flightNumber;
    private String date;
    private String airline;

    public RouteLeg(String from, String to, String flightNumber, String date, String airline) {
        this.from = from;
        this.to = to;
        this.flightNumber = flightNumber;
        this.date = date;
        this.airline = airline;
    }

    public String getFrom() { return from; }
    public String getTo() { return to; }
    public String getFlightNumber() { return flightNumber; }
    public String getDate() { return date; }
    public String getAirline() { return airline; }

    public String key() {
        return from + "|" + to + "|" + flightNumber + "|" + date + "|" + airline;
    }

    @Override
    public String toString() {
        return from + "->" + to + " (" + date + ", " + flightNumber + ", " + airline + ")";
    }
}
