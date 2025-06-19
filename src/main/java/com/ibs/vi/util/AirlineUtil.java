package com.ibs.vi.util;

import com.ibs.vi.model.Airline;

public class AirlineUtil {

    private static final String PREFIX = "AIRLINE";
    private static final String DELIMITER = "-";
    public static String generateKey(Airline airline) {
        return String.join(DELIMITER, PREFIX, airline.getAirlineCode());
    }
}

