package com.ibs.vi.scheduler;

import com.ibs.vi.model.Segment;
import com.ibs.vi.model.SegmentWithLayover;
import com.ibs.vi.repository.RedisRepository;
import com.ibs.vi.service.RouteService;
import com.ibs.vi.service.VIService;
import com.ibs.vi.view.RouteView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class VISchedulerTask implements Runnable {

    @Autowired
    private VIService viService;

    @Autowired
    @Qualifier("routeManagementService")
    private RouteService routeService;

    @Autowired
    private RedisRepository redisRepository;

    @Override
    public void run() {
        System.out.println("Running VI generation task...");

        try {
            List<RouteView> routes = routeService.getAll();

            if (routes.isEmpty()) {
                System.out.println("No routes found to process.");
                return;
            }
            // Specify your fixed date range
            LocalDate startDate = LocalDate.of(2025, 8, 5);
            LocalDate endDate = LocalDate.of(2025, 8, 18); // inclusive

            for (RouteView route : routes) {
                String origin = route.getDepartureAirport();
                String destination = route.getArrivalAirport();

                // Loop through date range instead of i = 0 to 10
                for (LocalDate travelDate = startDate; !travelDate.isAfter(endDate); travelDate = travelDate.plusDays(1)) {
                    int pax = 1;

                    try {
                        List<List<SegmentWithLayover>> itineraries = viService.buildFilteredSegmentCombinations(
                                origin, destination, travelDate, pax
                        );

                        System.out.printf(
                                "Generated %d itineraries for %s -> %s on %s%n",
                                itineraries.size(), origin, destination, travelDate
                        );
                        String key = origin + "|" + destination + "|" + travelDate;
                        redisRepository.save("VI_ITINERARIES", key, itineraries);

                    } catch (Exception e) {
                        System.err.printf("Error for %s -> %s on %s: %s%n",
                                origin, destination, travelDate, e.getMessage());
                        e.printStackTrace();
                    }
                }
            }




        } catch (Exception ex) {
            System.err.println("Scheduler failed: " + ex.getMessage());
            ex.printStackTrace();
        }
        System.out.println("Completed VI itinerary generation.");
    }
}
