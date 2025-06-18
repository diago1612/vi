package com.ibs.vi.view;

import com.ibs.vi.model.Route;
import com.ibs.vi.util.RouteUtil;

public class RouteView {

    private String key;
    private String departureAirport;
    private String arrivalAirport;

    public RouteView(){}

    public RouteView(Route route){
        this.key = RouteUtil.generateKey(route);
        this.departureAirport = route.getDepartureAirport();
        this.arrivalAirport = route.getArrivalAirport();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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
}
