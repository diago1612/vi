import java.util.*;
import java.time.LocalDate;

public class VirtualInterlineFinder {

    static class Leg {
        String airine;
        String from;
        String to;
        LocalDate date;

        Leg(String airline, String from, String to, LocalDate date) {
            this.airine = airline;
            this.from = from;
            this.to = to;
            this.date = date;
        }

        String getKey() {
            return from + "|" + to + "|" + date + "|" + airine;
        }

        @Override
        public String toString() {
            return getKey();
        }
    }

    public static void main(String[] args) {
        Map<String, List<String>> airlineKeys = new HashMap<>();

        // Sample data (like Redis keys)
        airlineKeys.put("Indigo", Arrays.asList(
                "COK|BLR|2025-08-09",
                "COK|BLR|2025-08-10",
                "COK|HYD|2025-08-09",
                "BLR|SRX|2025-08-09",
                "COK|DXB|2025-08-09",
                "LND|MAD|2025-08-09"
        ));

        airlineKeys.put("AirIndia", Arrays.asList(
                "COK|BLR|2025-08-09",
                "COK|BLR|2025-08-10",
                "COK|HYD|2025-08-10",
                "BLR|SRX|2025-08-09",
                "HYD|BLR|2025-08-10"
        ));

        airlineKeys.put("RYANAIR", Arrays.asList(
                "BLR|SRX|2025-08-11"
        ));

        String source = "COK";
        String destination = "SRX";

        Set<Leg> allLegs = new HashSet<>();

        for (Map.Entry<String, List<String>> entry : airlineKeys.entrySet()) {
            String airline = entry.getKey();
            for (String key : entry.getValue()) {
                String[] parts = key.split("\\|");
                allLegs.add(new Leg(airline, parts[0], parts[1], LocalDate.parse(parts[2])));
            }
        }

        Map<String, List<Leg>> graph = new HashMap<>();
        for (Leg leg : allLegs) {
            graph.computeIfAbsent(leg.from, k -> new ArrayList<>()).add(leg);
        }
        Set<Leg> validLegs = new HashSet<>();
        Deque<Leg> path = new ArrayDeque<>();


        dfs(source, destination, LocalDate.MIN, graph, path, validLegs, new HashSet<>());

        System.out.println("Valid VI Legs:");
        validLegs.stream()
                .sorted(Comparator.comparing(Leg::getKey))
                .forEach(System.out::println);
    }

    static void dfs(String current, String destination, LocalDate minDate,
                    Map<String, List<Leg>> graph, Deque<Leg> path,
                    Set<Leg> validLegs, Set<String> visited) {

        if (!graph.containsKey(current)) return;

        for (Leg leg : graph.get(current)) {
            if (leg.date.isBefore(minDate)) continue;

            String uniqueId = leg.getKey();
            if (visited.contains(uniqueId)) continue;

            visited.add(uniqueId);
            path.addLast(leg);

            if (leg.to.equals(destination)) {
                validLegs.addAll(new ArrayList<>(path));
            } else {
                dfs(leg.to, destination, leg.date, graph, path, validLegs, visited);
            }

            path.removeLast();
            visited.remove(uniqueId);
        }
    }
}

