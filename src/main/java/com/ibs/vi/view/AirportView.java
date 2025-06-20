package com.ibs.vi.view;

import com.ibs.vi.model.Airport;

public class AirportView {

    private String key;
    private String code;
    private String name;
    private String city;
    private String country;

    public AirportView(Airport airport) {
        this.key = airport.getCode(); // Using the airport code as the key, can be changed to separate key in future
        this.code = airport.getCode();
        this.name = airport.getName();
        this.city = airport.getCity();
        this.country = airport.getCountry();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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
}
