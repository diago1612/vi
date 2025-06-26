package com.ibs.vi.model;

import java.util.List;

public class RouteResult {
    private List<List<RouteLeg>> allPaths;
    private List<RouteLeg> uniqueLegs;

    public RouteResult(List<List<RouteLeg>> allPaths, List<RouteLeg> uniqueLegs) {
        this.allPaths = allPaths;
        this.uniqueLegs = uniqueLegs;
    }

    public List<List<RouteLeg>> getAllPaths() {
        return allPaths;
    }

    public List<RouteLeg> getUniqueLegs() {
        return uniqueLegs;
    }
}
