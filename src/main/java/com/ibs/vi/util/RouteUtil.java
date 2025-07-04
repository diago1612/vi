package com.ibs.vi.util;

import com.ibs.vi.model.Route;
import com.ibs.vi.model.Segment;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

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
        String departureDateOnly = segment.getDepartureTime().split("T")[0];
        key.append(segment.getDepartureAirportCode());
        key.append(SEGMENT_DELIMITER);
        key.append(segment.getArrivalAirportCode());
        key.append(SEGMENT_DELIMITER);
        key.append(segment.getFlightNumber());
        key.append(SEGMENT_DELIMITER);
        key.append(departureDateOnly);
        return key.toString();
    }

    public static String generateSortedSegmentKey(Segment segment){
        StringBuilder key = new StringBuilder();
        String departureDateOnly = segment.getDepartureTime().split("T")[0];
        key.append(segment.getDepartureAirportCode());
        key.append(SEGMENT_DELIMITER);
        key.append(segment.getArrivalAirportCode());
        key.append(SEGMENT_DELIMITER);
        key.append(segment.getFlightNumber());
        key.append(SEGMENT_DELIMITER);
        key.append(departureDateOnly);
        key.append(SEGMENT_DELIMITER);
        key.append(segment.getAirlineCode());
        return key.toString();
    }

    public static double parseDepartureTimeToEpoch(String departureTime) {
        // Adjust format to match your actual time format
        LocalDateTime dateTime = LocalDateTime.parse(departureTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return dateTime.toEpochSecond(ZoneOffset.UTC);
    }
}
