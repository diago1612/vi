package com.ibs.vi.model;

public class Airport {
    private String code;
    private String name;
    private String city;
    private String country;

    public Airport() {
    }

    public Airport(String code, String name, String city, String country) {
        this.code = code;
        this.name = name;
        this.city = city;
        this.country = country;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "Airport{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                '}';
    }

    public static Airport fromDepartureSegment(Segment segment) {
        Airport airport = new Airport();
        airport.setCode(segment.getDepartureAirportCode());
        airport.setName(segment.getDepartureAirportName()); // if available
        airport.setCity(null);   // or set it if available
        airport.setCountry(null);
        return airport;
    }

    public static Airport fromArrivalSegment(Segment segment) {
        Airport airport = new Airport();
        airport.setCode(segment.getArrivalAirportCode());
        airport.setName(segment.getArrivalAirportName()); // if available
        airport.setCity(null);   // or set it if available
        airport.setCountry(null);
        return airport;
    }

}
