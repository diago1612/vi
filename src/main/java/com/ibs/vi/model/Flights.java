package com.ibs.vi.model;

import java.util.List;

public class Flights {
    private int id;
    private FlightPoint departure;
    private FlightPoint arrival;
    private int stops;
    private String duration;
    private String fareType;
    private String currency;
   // private List<SegmentWithLayover> segments;
    private List<SegmentWithLayover> segments;
    private Double price;
    private List<String> airlines;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public FlightPoint getDeparture() {
        return departure;
    }

    public void setDeparture(FlightPoint departure) {
        this.departure = departure;
    }

    public FlightPoint getArrival() {
        return arrival;
    }

    public void setArrival(FlightPoint arrival) {
        this.arrival = arrival;
    }

    public int getStops() {
        return stops;
    }

    public void setStops(int stops) {
        this.stops = stops;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getFareType() {
        return fareType;
    }

    public void setFareType(String fareType) {
        this.fareType = fareType;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public List<SegmentWithLayover> getSegments() {
        return segments;
    }

    public void setSegments(List<SegmentWithLayover> segments) {
        this.segments = segments;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public List<String> getAirlines() {
        return airlines;
    }

    public void setAirlines(List<String> airlines) {
        this.airlines = airlines;
    }
}


