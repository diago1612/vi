package com.ibs.vi.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibs.vi.model.Segment;

import java.util.*;

public class FlightOfferMapper {

    public static List<Segment> parseFlightOffers(String jsonResponse) {
        List<Segment> Segments = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode root = mapper.readTree(jsonResponse);
            JsonNode data = root.get("data");

            if (data != null && data.isArray()) {
                for (JsonNode offer : data) {
                    JsonNode itineraries = offer.get("itineraries");
                    if (itineraries != null && itineraries.isArray()) {
                        JsonNode segments = itineraries.get(0).get("segments");
                        if (segments != null && segments.isArray()) {
                            JsonNode firstSegment = segments.get(0);
                            JsonNode lastSegment = segments.get(segments.size() - 1);

                            Segment flight = new Segment();

                            flight.flightNumber = firstSegment.get("number").asText();
                            flight.airlineCode = firstSegment.get("carrierCode").asText();
                            flight.departureAirportCode = firstSegment.get("departure").get("iataCode").asText();
                            flight.arrivalAirportCode = lastSegment.get("arrival").get("iataCode").asText();
                            flight.departureTime = firstSegment.get("departure").get("at").asText();
                            flight.arrivalTime = lastSegment.get("arrival").get("at").asText();

                            // Price and currency
                            flight.fare = offer.get("price").get("total").asText();
                            flight.currency = offer.get("price").get("currency").asText();

                            // Bookable seats
                            flight.availableSeats = offer.get("numberOfBookableSeats").asText();

                            Segments.add(flight);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return Segments;
    }
}

