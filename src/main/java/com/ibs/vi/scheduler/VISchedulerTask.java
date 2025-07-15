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

            LocalDate today = LocalDate.now();

            for (RouteView route : routes) {
                String origin = route.getDepartureAirport();
                String destination = route.getArrivalAirport();

                for (int i = 0; i <= 10; i++) {
                    LocalDate travelDate = today.plusDays(i);
                    int pax = 1;

                    try {
                        List<List<SegmentWithLayover>> itineraries = viService.buildFilteredSegmentCombinations(
                                origin, destination, travelDate, pax
                        );

                        System.out.printf(
                                "Generated %d itineraries for %s -> %s on %s%n",
                                itineraries.size(), origin, destination, travelDate
                        );
                        String key = origin + "|" + destination + "|" + travelDate.toString();
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
