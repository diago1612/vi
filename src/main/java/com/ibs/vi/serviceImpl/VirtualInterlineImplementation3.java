package com.ibs.vi.serviceImpl;

import com.ibs.vi.model.RouteLeg;
import com.ibs.vi.model.RouteResult;
import com.ibs.vi.model.Segment;
import com.ibs.vi.service.VIRouteLogic;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ibs.vi.util.VIUtil.buildAllLegs;
import static com.ibs.vi.util.VIUtil.buildGraph;
import static com.ibs.vi.util.VIUtil.findValidPaths;

@Service
public class VirtualInterlineImplementation3 {

    private static SegmentInterlineService segmentInterlineService;

    private static final Duration MIN_LAYOVER = Duration.ofHours(2);

    // get all the keys from the ZADD;

    public static RouteResult buildRoutesWithLegs(String source, String destination, List<String> zaddKeys) {

        List<RouteLeg> allLegs = new ArrayList<>();

        for (String entry : zaddKeys) {
            String[] parts = entry.split("\\|");
            if (parts.length == 5) {
                String from = parts[0];
                String to = parts[1];
                String flightNumber = parts[2];
                String date = parts[3];
                String airline = parts[4];

                allLegs.add(new RouteLeg(from, to, flightNumber, date, airline));
            }
        }

        Map<String, List<RouteLeg>> graph = buildGraph(allLegs);

        List<List<RouteLeg>> validPaths = new ArrayList<>();
        findValidPaths(graph, source, destination, validPaths);

        List<RouteLeg> uniqueLegs = validPaths.stream()
                .flatMap(List::stream)
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(RouteLeg::key, leg -> leg, (a, b) -> a),
                        map -> new ArrayList<>(map.values())
                ));

        return new RouteResult(validPaths, uniqueLegs);
    }

    public static List<Segment> mapRouteLegsToSegments(List<RouteLeg> uniqueLegs) {
        SegmentServiceImpl segmentServiceimpl = new SegmentServiceImpl();
        String[] segmentKeys = uniqueLegs.stream()
                .map(leg -> leg.getFrom() + "|" + leg.getTo() + "|" + leg.getDate() + "|" + leg.getFlightNumber())
                .toArray(String[]::new);

        return segmentServiceimpl.viSegmentDetails(segmentKeys);
    }

    // now we need to get the details from the segments using the result from buildRoutesWithLegs keys

    // create a list of airlines from the keys that we get from the above result

    // get all the details from the segments and store in list

    public static void main(String[] args) {
        List<String> zaddKeys = Arrays.asList(
                "COK|MAA|LM511|2025-06-01|6E",
                "MAA|DEL|AI202|2025-06-01|AI",
                "DEL|BLR|SG305|2025-06-02|SG",
                "BLR|HYD|6E907|2025-06-02|6E",
                "HYD|COK|AI118|2025-06-03|AI",
                "COK|BLR|SG100|2025-06-01|SG",
                "BLR|MAA|6E200|2025-06-02|6E",
                "MAA|HYD|AI505|2025-06-01|AI",
                "DEL|COK|SG801|2025-06-03|SG",
                "HYD|DEL|6E311|2025-06-02|6E"
        );

        RouteResult result = buildRoutesWithLegs("COK", "DEL", zaddKeys);

        System.out.println("All Valid Paths:\n");

        for (List<RouteLeg> path : result.getAllPaths()) {
            String pathStr = path.stream()
                    .map(RouteLeg::toString)
                    .collect(Collectors.joining(" -> "));
            System.out.println(pathStr);
        }
        System.out.println("\nUnique Legs Across All Routes:\n");

        for (RouteLeg leg : result.getUniqueLegs()) {
            System.out.println("\"" + leg.key() + "\",");
        }

        List<Segment> SegmentDetails= mapRouteLegsToSegments(result.getUniqueLegs());
        List<List<Segment>> finaldetails = segmentInterlineService.generateItineraries(SegmentDetails,"COK","DEL", LocalDate.ofEpochDay(2025-06-01), 1);

        System.out.println("Segment Details:\n");

        for (Segment segment : SegmentDetails) {
            System.out.println("Flight Number: " + segment.getFlightNumber());
            System.out.println("Airline: " + segment.getAirlineCode());
            System.out.println("From: " + segment.getDepartureAirportName());
            System.out.println("To: " + segment.getArrivalAirportName());
            System.out.println("Departure Time: " + segment.getDepartureTime());
            System.out.println("Arrival Time: " + segment.getArrivalTime());
            System.out.println("Fare: " + segment.getFare());
            System.out.println("Available Seats: " + segment.getAvailableSeats());
            System.out.println("----------------------------");
        }

        int itineraryCount = 1;
        for (List<Segment> itinerary : finaldetails) {
            System.out.println("Itinerary " + itineraryCount++ + ":");
            for (Segment segment : itinerary) {
                System.out.println(segment);
            }
            System.out.println("-----");
        }
    }

}
