package com.ibs.vi.util;

import com.ibs.vi.model.Airline;
import com.ibs.vi.model.Route;
import com.ibs.vi.model.Segment;

public class RouteUtil {
    private static final String DELIMITER = "-";
    private static final String SEGMENT_DELIMITER = "|";

    public static String generateKey(Route route){
        StringBuilder key = new StringBuilder();
        key.append(route.getDepartureAirport());
        key.append(DELIMITER);
        key.append(route.getArrivalAirport());
        return key.toString();
    }

    public static String generateSegmentKey(Segment segment){
        StringBuilder key = new StringBuilder();
        key.append(segment.getDepartureAirportCode());
        key.append(SEGMENT_DELIMITER);
        key.append(segment.getArrivalAirportCode());
        key.append(SEGMENT_DELIMITER);
        key.append(segment.getDate());
        return key.toString();
    }
}
