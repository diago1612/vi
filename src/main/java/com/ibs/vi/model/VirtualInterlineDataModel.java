package com.ibs.vi.model;

public class VirtualInterlineDataModel {
    
    private String departure;
    private String arrival;
    private String flight;
    private String date;
    private int fare;
    
    public VirtualInterlineDataModel(){}

    public VirtualInterlineDataModel(String departure, String arrival, String flight, String date, int fare) {
        this.departure = departure;
        this.arrival = arrival;
        this.flight = flight;
        this.date = date;
        this.fare = fare;
    }

    /**
     * @return the departure
     */
    public String getDeparture() {
        return departure;
    }

    /**
     * @param departure the departure to set
     */
    public void setDeparture(String departure) {
        this.departure = departure;
    }

    /**
     * @return the arrival
     */
    public String getArrival() {
        return arrival;
    }

    /**
     * @param arrival the arrival to set
     */
    public void setArrival(String arrival) {
        this.arrival = arrival;
    }

    /**
     * @return the flight
     */
    public String getFlight() {
        return flight;
    }

    /**
     * @param flight the flight to set
     */
    public void setFlight(String flight) {
        this.flight = flight;
    }

    /**
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * @return the fare
     */
    public int getFare() {
        return fare;
    }

    /**
     * @param fare the fare to set
     */
    public void setFare(int fare) {
        this.fare = fare;
    }
    
    @Override
     	public String toString() {
         	return String.format("%s -> %s (%s, %s, ₹%d)", departure, arrival, flight, date, fare);
     	}
}
