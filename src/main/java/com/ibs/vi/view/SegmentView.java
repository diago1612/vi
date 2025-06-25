package com.ibs.vi.view;

import com.ibs.vi.model.Segment;

public class SegmentView {
    private String departureAirportCode;
    private String arrivalAirportCode;
    private String date;
    private String airportCode;

    public SegmentView() {}
    public SegmentView(Segment segment) {
        this.departureAirportCode = segment.getDepartureAirportCode();
        this.arrivalAirportCode = segment.getArrivalAirportCode();
        this.date=segment.getDate();
        this.airportCode = segment.getAirportCode();
    }

    public String getDepartureAirportCode() {
        return departureAirportCode;
    }

    public void setDepartureAirportCode(String departureAirportCode) {
        this.departureAirportCode = departureAirportCode;
    }

    public String getArrivalAirportCode() {
        return arrivalAirportCode;
    }

    public void setArrivalAirportCode(String arrivalAirportCode) {
        this.arrivalAirportCode = arrivalAirportCode;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAirportCode() {
        return airportCode;
    }

    public void setAirportCode(String airportCode) {
        this.airportCode = airportCode;
    }
}
