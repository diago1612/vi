package com.ibs.vi.util;

import com.ibs.vi.model.RouteLeg;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class VIUtil {

    public static List<RouteLeg> buildAllLegs(Map<String, List<String>> airlineKeys) {
        List<RouteLeg> allLegs = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : airlineKeys.entrySet()) {
            String airline = entry.getKey();
            for (String key : entry.getValue()) {
                String[] parts = key.split("\\|");
                if (parts.length == 3) {
                    allLegs.add(new RouteLeg(parts[0], parts[1], parts[2], airline));
                }
            }
        }
        return allLegs;
    }

    // Step 2: Build graph from legs
    public static Map<String, List<RouteLeg>> buildGraph(List<RouteLeg> legs) {
        Map<String, List<RouteLeg>> graph = new HashMap<>();
        for (RouteLeg leg : legs) {
            graph.computeIfAbsent(leg.getFrom(), k -> new ArrayList<>()).add(leg);
        }
        return graph;
    }

    public static List<RouteLeg> findValidPaths(Map<String, List<RouteLeg>> graph, String source, String destination, List<List<RouteLeg>> validPaths) {
        Set<String> visited = new HashSet<>();
        List<RouteLeg> path = new ArrayList<>();
        List<RouteLeg> validLegs = new ArrayList<>();

        dfs(graph, source, destination, visited, path, validLegs, validPaths, null);
        return validLegs;
    }

    // Step 4: DFS logic with constraints
    public static void dfs(
            Map<String, List<RouteLeg>> graph,
            String currentAirport,
            String destination,
            Set<String> visited,
            List<RouteLeg> path,
            List<RouteLeg> validLegs,
            List<List<RouteLeg>> validPaths,
            LocalDate previousDate
    ) {
        if (visited.contains(currentAirport)) return;

        visited.add(currentAirport);

        if (graph.containsKey(currentAirport)) {
            for (RouteLeg leg : graph.get(currentAirport)) {
                LocalDate legDate = LocalDate.parse(leg.getDate());

                // Check chronological order
                if (path.isEmpty() || !legDate.isBefore(previousDate)) {
                    path.add(leg);

                    // Limit: max 3 legs (i.e., max 2 stops)
                    if (path.size() > 3) {
                        path.remove(path.size() - 1);
                        continue;
                    }

                    if (leg.getTo().equals(destination)) {
                        validPaths.add(new ArrayList<>(path));
                        validLegs.addAll(path);
                    } else {
                        dfs(graph, leg.getTo(), destination, visited, path, validLegs, validPaths, legDate);
                    }

                    path.remove(path.size() - 1); // backtrack
                }
            }
        }

        visited.remove(currentAirport);
    }

    // Step 5: Print results
    public static void printResults(String source, String destination, List<List<RouteLeg>> validPaths, List<RouteLeg> validLegs) {
        System.out.println("Possible VI paths from " + source + " to " + destination + ":\n");
        for (List<RouteLeg> viPath : validPaths) {
            String route = viPath.stream()
                    .map(RouteLeg::toString)
                    .collect(Collectors.joining(" -> "));
            System.out.println(route);
        }

        System.out.println("\nKeys to retain in Redis:");
        Set<String> finalKeys = validLegs.stream()
                .map(RouteLeg::key)
                .collect(Collectors.toSet());
        finalKeys.forEach(System.out::println);
    }
}
