package com.ibs.vi.serviceImpl;

import com.ibs.vi.model.Segment;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SegmentInterlineService {

    private static final Duration MIN_LAYOVER = Duration.ofHours(2);

    public List<List<Segment>> generateItineraries(List<Segment> segments, String origin, String destination,  LocalDate departureDate, int pax) {
        List<List<Segment>> itineraries = new ArrayList<>();

        for (Segment segment : segments) {
            LocalDateTime depTime = LocalDateTime.parse(segment.getDepartureTime());
            if (segment.getDepartureAirportCode().equalsIgnoreCase(origin)
                    && depTime.toLocalDate().equals(departureDate)
                    && Integer.parseInt(segment.getAvailableSeats()) >= pax) {

                List<Segment> path = new ArrayList<>();
                path.add(segment);
                buildItineraries(path, segments, itineraries, destination, pax);
            }
        }

        return itineraries;
    }

    private void buildItineraries(List<Segment> currentPath, List<Segment> allSegments,
                                  List<List<Segment>> allItineraries, String destination, int pax) {
        Segment lastSegment = currentPath.get(currentPath.size() - 1);

        if (lastSegment.getArrivalAirportCode().equalsIgnoreCase(destination)) {
            allItineraries.add(new ArrayList<>(currentPath));
            return;
        }

        for (Segment next : allSegments) {
            if (currentPath.contains(next)) continue;
            if (!lastSegment.getArrivalAirportCode().equalsIgnoreCase(next.getDepartureAirportCode())) continue;

            LocalDateTime arrivalTime = LocalDateTime.parse(lastSegment.getArrivalTime());
            LocalDateTime nextDepartureTime = LocalDateTime.parse(next.getDepartureTime());
            Duration layover = Duration.between(arrivalTime, nextDepartureTime);
            if (layover.compareTo(MIN_LAYOVER) < 0) continue;
            try {
                int availableSeats = Integer.parseInt(next.getAvailableSeats());
                if (availableSeats < pax) continue;
            } catch (NumberFormatException | NullPointerException e) {
                continue;
            }

            List<Segment> newPath = new ArrayList<>(currentPath);
            newPath.add(next);
            buildItineraries(newPath, allSegments, allItineraries, destination, pax);
        }
    }
}

