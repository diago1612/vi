package com.ibs.vi.model;

public class RouteLeg {
    String from;
    String to;
    String date;
    String airline;

    public RouteLeg(String from, String to, String date, String airline) {
        this.from = from;
        this.to = to;
        this.date = date;
        this.airline = airline;
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

    public String getAirline() {
        return airline;
    }

    public void setAirline(String airline) {
        this.airline = airline;
    }

    public String key() {
        return from + "|" + to + "|" + date + "|" + airline;
    }

    @Override
    public String toString() {
        return from + "->" + to + "(" + date + "," + airline + ")";
    }
}
