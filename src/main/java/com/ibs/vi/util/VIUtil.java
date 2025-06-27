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

    public static List<RouteLeg> buildAllLegs(List<String> rawKeys) {
        List<RouteLeg> allLegs = new ArrayList<>();
        for (String entry : rawKeys) {
            String[] parts = entry.split("\\|");
            if (parts.length == 5) {
                allLegs.add(new RouteLeg(parts[0], parts[1], parts[2], parts[3], parts[4]));
            }
        }
        return allLegs;
    }

    public static Map<String, List<RouteLeg>> buildGraph(List<RouteLeg> legs) {
        Map<String, List<RouteLeg>> graph = new HashMap<>();
        for (RouteLeg leg : legs) {
            graph.computeIfAbsent(leg.getFrom(), k -> new ArrayList<>()).add(leg);
        }
        return graph;
    }

    public static void findValidPaths(
            Map<String, List<RouteLeg>> graph,
            String source,
            String destination,
            List<List<RouteLeg>> validPaths
    ) {
        Set<String> visited = new HashSet<>();
        dfs(graph, source, destination, visited, new ArrayList<>(), validPaths, null, null);
    }

    private static void dfs(
            Map<String, List<RouteLeg>> graph,
            String current,
            String destination,
            Set<String> visited,
            List<RouteLeg> path,
            List<List<RouteLeg>> validPaths,
            LocalDate firstDate,
            LocalDate prevDate
    ) {
        if (visited.contains(current)) return;

        visited.add(current);

        if (graph.containsKey(current)) {
            for (RouteLeg leg : graph.get(current)) {
                LocalDate legDate = LocalDate.parse(leg.getDate());

                if (path.isEmpty()) {
                    firstDate = legDate;
                }

                if (path.isEmpty() || (!legDate.isBefore(prevDate))) {
                    if (firstDate != null && legDate.isAfter(firstDate.plusDays(2))) continue;

                    path.add(leg);

                    if (path.size() > 4) {
                        path.remove(path.size() - 1);
                        continue;
                    }

                    if (leg.getTo().equals(destination)) {
                        validPaths.add(new ArrayList<>(path));
                    } else {
                        dfs(graph, leg.getTo(), destination, visited, path, validPaths, firstDate, legDate);
                    }

                    path.remove(path.size() - 1); // backtrack
                }
            }
        }

        visited.remove(current);
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
