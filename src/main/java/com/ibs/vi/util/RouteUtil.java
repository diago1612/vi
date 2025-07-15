package com.ibs.vi.util;

import com.ibs.vi.model.Route;
import com.ibs.vi.model.Segment;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        key.append(segment.getDepartureAirport());
        key.append(SEGMENT_DELIMITER);
        key.append(segment.getArrivalAirport());
        key.append(SEGMENT_DELIMITER);
        key.append(segment.getFlightNumber());
        key.append(SEGMENT_DELIMITER);
        key.append(departureDateOnly);
        key.append(SEGMENT_DELIMITER);
        key.append(segment.getAirline());
        return key.toString();
    }

    public static String generateSortedSegmentKey(Segment segment){
        StringBuilder key = new StringBuilder();
        String departureDateOnly = segment.getDepartureTime().split("T")[0];
        key.append(segment.getDepartureAirport());
        key.append(SEGMENT_DELIMITER);
        key.append(segment.getArrivalAirport());
        key.append(SEGMENT_DELIMITER);
        key.append(segment.getFlightNumber());
        key.append(SEGMENT_DELIMITER);
        key.append(departureDateOnly);
        key.append(SEGMENT_DELIMITER);
        key.append(segment.getAirline());
        return key.toString();
    }

    public static double parseDepartureTimeToEpoch(String departureTime) {
        // Adjust format to match your actual time format
        LocalDateTime dateTime = LocalDateTime.parse(departureTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return dateTime.toEpochSecond(ZoneOffset.UTC);
    }

    public static Map<String, List<String>> generateSegmentKeyMap(String... keys){
        if(keys == null || keys.length == 0){
            return null;
        }
        Map<String, List<String>> resultMap = Arrays.stream(keys)
                .collect(Collectors.groupingBy(
                        line -> {
                            String[] parts = line.split("\\|");
                            return parts[parts.length - 1]; // last part as key
                        }
                ));

        return resultMap;
    }
}
