package com.ibs.vi.util;

import com.ibs.vi.model.Route;

public class RouteUtil {
    private static final String DELIMITER = "-";

    public static String generateKey(Route route){
        StringBuilder key = new StringBuilder();
        key.append(route.getDepartureAirport());
        key.append(DELIMITER);
        key.append(route.getArrivalAirport());
        return key.toString();
    }
}
