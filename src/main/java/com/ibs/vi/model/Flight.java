package com.ibs.vi.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Duration;
import java.time.LocalDateTime;

public class Flight {
    public String flightNumber;
    public String airline;
    public String departureAirport;
    public String arrivalAirport;
    public String departureAirportName;
    public String arrivalAirportName;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    public LocalDateTime departureTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    public LocalDateTime arrivalTime;
    public Layover layover;

    public Flight() {
    }

    public Flight(Flight other) {
        this.flightNumber = other.flightNumber;
        this.airline = other.airline;
        this.departureAirport = other.departureAirport;
        this.arrivalAirport = other.arrivalAirport;
        this.departureAirportName = other.departureAirportName;
        this.arrivalAirportName = other.arrivalAirportName;
        this.departureTime = other.departureTime;
        this.arrivalTime = other.arrivalTime;
        this.fare = other.fare;
        this.availableSeats = other.availableSeats;
        this.layover = other.layover; // NOTE: shallow copy – deep copy if needed
    }

    public Layover getLayover() {
        return layover;
    }

    public void setLayover(Layover layover) {
        this.layover = layover;
    }

    public int getFare() {
        return fare;
    }

    public void setFare(int fare) {
        this.fare = fare;
    }

    public int fare;

    public int availableSeats;

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public String getArrivalAirport() {
        return arrivalAirport;
    }

    public void setArrivalAirport(String arrivalAirport) {
        this.arrivalAirport = arrivalAirport;
    }

    public String getDepartureAirport() {
        return departureAirport;
    }

    public void setDepartureAirport(String departureAirport) {
        this.departureAirport = departureAirport;
    }

    public String getAirline() {
        return airline;
    }

    public void setAirline(String airline) {
        this.airline = airline;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
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
}
