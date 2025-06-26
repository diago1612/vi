package com.ibs.vi.util;

import com.ibs.vi.model.Route;
import com.ibs.vi.model.Segment;

public class RouteUtil {
    private static final String DELIMITER = "-";

    public static String generateKey(Route route){
        StringBuilder key = new StringBuilder();
        key.append(route.getDepartureAirport());
        key.append(DELIMITER);
        key.append(route.getArrivalAirport());
        return key.toString();
    }

    public static String generateKeyForSegments(Segment segment) {
        String departureDate = segment.getDepartureTime().split("T")[0]; // Extract only date part
        return String.join("|",
                segment.getDepartureAirport(),
                segment.getArrivalAirport(),
                segment.getFlightNumber(),
                departureDate);
    }


}
