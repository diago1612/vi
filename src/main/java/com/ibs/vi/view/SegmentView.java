package com.ibs.vi.view;

import com.ibs.vi.model.Segment;
import com.ibs.vi.util.RouteUtil;

public class SegmentView {

    public SegmentView(Segment segment) {
        this.key = RouteUtil.generateSegmentKey(segment);
        this.flightNumber = segment.getFlightNumber();
        this.airlineCode = segment.getAirlineCode();
        this.departureAirport = segment.getDepartureAirportCode();
        this.arrivalAirport = segment.getArrivalAirportCode();
        this.departureAirportName = segment.getDepartureAirportName();
        this.arrivalAirportName = segment.getArrivalAirportName();
        this.departureTime = segment.getDepartureTime();
        this.arrivalTime = segment.getArrivalTime();
        this.fare = segment.getFare();
        this.availableSeats = segment.getAvailableSeats();
    }

    public String key;
    public String flightNumber;

    public String airlineCode;
    public String departureAirport;
    public String arrivalAirport;
    public String departureAirportName;
    public String arrivalAirportName;
    public String departureTime;
    public String arrivalTime;
    public String fare;
    public String availableSeats;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getAirlineCode() {
        return airlineCode;
    }

    public void setAirlineCode(String airlineCode) {
        this.airlineCode = airlineCode;
    }

    public String getDepartureAirport() {
        return departureAirport;
    }

    public void setDepartureAirport(String departureAirport) {
        this.departureAirport = departureAirport;
    }

    public String getArrivalAirport() {
        return arrivalAirport;
    }

    public void setArrivalAirport(String arrivalAirport) {
        this.arrivalAirport = arrivalAirport;
    }

    public String getDepartureAirportName() {
        return departureAirportName;
    }

    public void setDepartureAirportName(String departureAirportName) {
        this.departureAirportName = departureAirportName;
    }

    public String getArrivalAirportName() {
        return arrivalAirportName;
    }

    public void setArrivalAirportName(String arrivalAirportName) {
        this.arrivalAirportName = arrivalAirportName;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getFare() {
        return fare;
    }

    public void setFare(String fare) {
        this.fare = fare;
    }

    public String getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(String availableSeats) {
        this.availableSeats = availableSeats;
    }
}
