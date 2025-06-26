package com.ibs.vi.model;

public class RouteLeg {
    String from;
    String to;
    String date;
    String airlineCode;
    String flightNumber;

    public RouteLeg(String from, String to, String flightNumber, String date, String airlineCode) {
        this.from = from;
        this.to = to;
        this.flightNumber = flightNumber;
        this.date = date;
        this.airlineCode = airlineCode;


    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAirlineCode() {
        return airlineCode;
    }

    public void setAirlineCode(String airlineCode) {
        this.airlineCode = airlineCode;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String key() {
        return from + "|" + to + "|" + flightNumber + "|" + date + "|" + airlineCode;
    }

    @Override
    public String toString() {
        return from + "->" + to + "(" + date + "," + airlineCode + ")";
    }
}

