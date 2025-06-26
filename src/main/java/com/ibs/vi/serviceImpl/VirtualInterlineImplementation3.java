package com.ibs.vi.serviceImpl;

import com.ibs.vi.model.RouteLeg;
import com.ibs.vi.model.RouteResult;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ibs.vi.util.VIUtil.buildAllLegs;
import static com.ibs.vi.util.VIUtil.buildGraph;
import static com.ibs.vi.util.VIUtil.findValidPaths;

public class VirtualInterlineImplementation3 {

    private static final Duration MIN_LAYOVER = Duration.ofHours(2);

    // get all the keys from the ZADD;

    List<String> zaddKeys = Arrays.asList(
            "COK|BLR|2025-08-09|Indigo",
            "COK|BLR|2025-08-10|SpiceJet",
            "COK|HYD|2025-08-09|Indigo",
            "BLR|SRX|2025-08-09|AirIndia",
            "COK|DXB|2025-08-09|SpiceJet",
            "LND|MAD|2025-08-09|Indigo",
            "COK|BLR|2025-08-09|AirIndia",
            "COK|BLR|2025-08-10|Indigo",
            "COK|HYD|2025-08-10|SpiceJet",
            "BLR|SRX|2025-08-09|Indigo",
            "HYD|BLR|2025-08-10|AirIndia",
            "BLR|SRX|2025-08-11|SpiceJet"
    );

    public static RouteResult buildRoutesWithLegs(String source, String destination, List<String> zaddKeys) {

        Map<String, List<String>> airlineKeyMap = new HashMap<>();
        for (String entry : zaddKeys) {
            String[] parts = entry.split("\\|");
            if (parts.length == 4) {
                String routeKey = String.join("|", parts[0], parts[1], parts[2]); // from|to|date
                String airline = parts[3];
                airlineKeyMap.computeIfAbsent(airline, k -> new ArrayList<>()).add(routeKey);
            }
        }

        List<RouteLeg> allLegs = buildAllLegs(airlineKeyMap);
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

    // now we need to get the details from the segments using the result from buildRoutesWithLegs keys

    // create a list of airlines from the keys that we get from the above result

    // get all the details from the segments and store in list

    public static void main(String[] args) {
        List<String> zaddKeys = Arrays.asList(
                "COK|BLR|2025-08-09|Indigo",
                "COK|BLR|2025-08-10|SpiceJet",
                "COK|HYD|2025-08-09|Indigo",
                "BLR|SRX|2025-08-09|AirIndia",
                "COK|DXB|2025-08-09|SpiceJet",
                "LND|MAD|2025-08-09|Indigo",
                "COK|BLR|2025-08-09|AirIndia",
                "COK|BLR|2025-08-10|Indigo",
                "COK|HYD|2025-08-10|SpiceJet",
                "BLR|SRX|2025-08-09|Indigo",
                "HYD|BLR|2025-08-10|AirIndia",
                "BLR|SRX|2025-08-11|SpiceJet"
        );

        RouteResult result = buildRoutesWithLegs("COK", "SRX", zaddKeys);

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
    }

}
