package com.ibs.vi.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class Flight {
    public String flightNumber;
    public String airline;
    public String departureAirport;
    public String arrivalAirport;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    public LocalDateTime departureTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    public LocalDateTime arrivalTime;

    @Override
    public String toString() {
        return flightNumber + ": " + departureAirport + " -> " + arrivalAirport +
                " | " + departureTime + " to " + arrivalTime;
    }
}
