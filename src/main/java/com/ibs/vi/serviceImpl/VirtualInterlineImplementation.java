package com.ibs.vi.serviceImpl;

import com.ibs.vi.model.VirtualInterlineDataModel;
import com.ibs.vi.service.VirtualInterlineService;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class VirtualInterlineImplementation implements VirtualInterlineService {

    @Override
    public String getHealthStatus() {
        return "Application is healthy";
    }

    @Override
    public List<List<VirtualInterlineDataModel>> fetchFlightDetails(String source, String destination, String startDate) throws ParseException {
        
        List<VirtualInterlineDataModel> flights = Arrays.asList(
         	new VirtualInterlineDataModel("kochi", "banglore", "Air India", "24/05/2025", 5000),
         	new VirtualInterlineDataModel("kochi", "delhi", "Air India", "24/05/2025", 5000),
         	new VirtualInterlineDataModel("delhi", "srinagar", "Air India", "25/05/2025", 5000),
         	new VirtualInterlineDataModel("kochi", "srinagar", "Air India", "26/05/2025", 5000),
         	new VirtualInterlineDataModel("kochi", "jammu", "Air India", "27/05/2025", 5000),
         	new VirtualInterlineDataModel("jammu", "delhi", "Air India", "26/05/2025", 5000)
     	);
        
        
        // Map of departure to list of flights
     	  Map<String, List<VirtualInterlineDataModel>> flightMap = new HashMap<>();
     	for (VirtualInterlineDataModel flight : flights) {
         	flightMap.computeIfAbsent(flight.getDeparture(), k -> new ArrayList<>()).add(flight);
     	}
 
    	// Result list
     	List<List<VirtualInterlineDataModel>> results = new ArrayList<>();
        
        // Start DFS
     	dfs(source, destination, flightMap, new ArrayList<>(), new HashSet<>(), results, startDate);
 
    	// Output
        return results;
 	}
    
    
    
    
    private static void dfs(String current,
                         	String destination,
                         	Map<String, List<VirtualInterlineDataModel>> flightMap,
                         	List<VirtualInterlineDataModel> path,
                         	Set<String> visited,
                         	List<List<VirtualInterlineDataModel>> results,
                         	String currentDateStr) throws ParseException {
 
    	if (current.equals(destination)) {
         	results.add(new ArrayList<>(path));
         	return;
     	}
 
    	visited.add(current);
     	  SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
     	  Date currentDate = sdf.parse(currentDateStr);
 
    	if (flightMap.containsKey(current)) {
         	for (VirtualInterlineDataModel flight : flightMap.get(current)) {
             	Date flightDate = sdf.parse(flight.getDate());
             	if (!visited.contains(flight.getArrival()) && !flightDate.before(currentDate)) {
                 	path.add(flight);
                 	dfs(flight.getArrival(), destination, flightMap, path, visited, results, flight.getDate());
                 	path.remove(path.size() - 1); // Backtrack
             	}
         	}
     	}
 
    	visited.remove(current);
 	}
}
