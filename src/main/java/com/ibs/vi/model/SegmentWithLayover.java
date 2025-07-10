package com.ibs.vi.model;

public class SegmentWithLayover {
    public String flightNumber;

    public String airline;
    public String departureAirport;
    public String arrivalAirport;
    public String departureAirportName;
    public String arrivalAirportName;
    public String departureTime;
    public String arrivalTime;
    public String fare;
   // public String currency;
    public String availableSeats;
    public Layover layover;

    public SegmentWithLayover(Segment segment) {
    }

    public Layover getLayover() {
        return layover;
    }

    public void setLayover(Layover layover) {
        this.layover = layover;
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

   /* public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }*/

    public String getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(String availableSeats) {
        this.availableSeats = availableSeats;
    }

    @Override
    public String toString() {
        return "Segment{" +
                "flightNumber='" + flightNumber + '\'' +
                ", departure='" + departureAirport + '\'' +
                ", arrival='" + arrivalAirport + '\'' +
                ", departureDate='" + departureTime + '\'' +
                ", airline='" + airline + '\'' +
                ", fare=" + fare +
               // ", currency=" + currency +
                ", seats=" + availableSeats +
                '}';
    }
}
