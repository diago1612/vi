import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class VirtualInterlineFinder {

    static class Leg {
        String from;
        String to;
        String date;
        String airline;

        Leg(String from, String to, String date, String airline) {
            this.from = from;
            this.to = to;
            this.date = date;
            this.airline = airline;
        }

        String key() {
            return from + "|" + to + "|" + date + "|" + airline;
        }

        @Override
        public String toString() {
            return from + "->" + to + "(" + date + "," + airline + ")";
        }
    }

    public static void main(String[] args) {
        String source = "COK";
        String destination = "SRX";

        // Hardcoded keys similar to redi
        Map<String, List<String>> airlineKeys = Map.of(
                "Indigo", List.of(
                        "COK|BLR|2025-08-09",
                        "COK|BLR|2025-08-10",
                        "COK|HYD|2025-08-09",
                        "BLR|SRX|2025-08-09",
                        "COK|DXB|2025-08-09",
                        "LND|MAD|2025-08-09"
                ),
                "AirIndia", List.of(
                        "COK|BLR|2025-08-09",
                        "COK|BLR|2025-08-10",
                        "COK|HYD|2025-08-10",
                        "BLR|SRX|2025-08-09",
                        "HYD|BLR|2025-08-10"
                ),
                "RYANAIR", List.of(
                        "BLR|SRX|2025-08-11"
                )
        );

        // Build legs from all keys
        List<Leg> allLegs = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : airlineKeys.entrySet()) {
            String airline = entry.getKey();
            for (String key : entry.getValue()) {
                String[] parts = key.split("\\|");
                if (parts.length == 3) {
                    allLegs.add(new Leg(parts[0], parts[1], parts[2], airline));
                }
            }
        }

        // Build graph
        Map<String, List<Leg>> graph = new HashMap<>();
        for (Leg leg : allLegs) {
            graph.computeIfAbsent(leg.from, k -> new ArrayList<>()).add(leg);
        }

        // DFS to find all VI paths from source to destination
        Set<String> visited = new HashSet<>();
        List<Leg> path = new ArrayList<>();
        List<Leg> validLegs = new ArrayList<>();
        List<List<Leg>> validPaths = new ArrayList<>();

        dfs(graph, source, destination, visited, path, validLegs, validPaths, null);


        // Print possible full paths
        System.out.println("Possible VI paths from " + source + " to " + destination + ":\n");
        for (List<Leg> viPath : validPaths) {
            String route = viPath.stream()
                    .map(Leg::toString)
                    .collect(Collectors.joining(" -> "));
            System.out.println(route);
        }

        // Print keys to retain
        System.out.println(" Keys to retain in Redis:");
        Set<String> finalKeys = validLegs.stream()
                .map(Leg::key)
                .collect(Collectors.toSet());
        finalKeys.forEach(System.out::println);
    }

    private static void dfs(
            Map<String, List<Leg>> graph,
            String currentAirport,
            String destination,
            Set<String> visited,
            List<Leg> path,
            List<Leg> validLegs,
            List<List<Leg>> validPaths,
            LocalDate previousDate
    ) {
        if (visited.contains(currentAirport)) return;

        visited.add(currentAirport);

        if (graph.containsKey(currentAirport)) {
            for (Leg leg : graph.get(currentAirport)) {
                LocalDate legDate = LocalDate.parse(leg.date);

                // chronological order of date
                if (path.isEmpty() || !legDate.isBefore(previousDate)) {

                    path.add(leg);

                    // Skip paths with more than 3 legs (2 stops)
                    if (path.size() > 3) {
                        path.remove(path.size() - 1);
                        continue;
                    }

                    if (leg.to.equals(destination)) {
                        validPaths.add(new ArrayList<>(path));
                        validLegs.addAll(path);
                    } else {
                        dfs(graph, leg.to, destination, visited, path, validLegs, validPaths, legDate);
                    }

                    path.remove(path.size() - 1); // backtrack
                }
            }
        }

        visited.remove(currentAirport);
    }
}
