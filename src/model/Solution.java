package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Solution {
    private List<List<Integer>> routes;
    private int cost;


    public Solution(List<List<Integer>> routes, Instance instance) {
        this.routes = routes;
        this.cost = calculateCost(instance);
    }

    // Deep copy constructor
    public Solution(Solution other) {
        this.routes = new ArrayList<>();
        for (List<Integer> route : other.routes) {
            this.routes.add(new ArrayList<>(route));
        }

        this.cost = other.cost;
    }

    public List<List<Integer>> getRoutes() {
        return routes;
    }

    public void setRoutes(List<List<Integer>> routes) {
        this.routes = routes;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }


    public int calculateCost(Instance instance) {
        int totalCost = 0;
        int depotId = instance.getDepotId();

        for (List<Integer> route : routes) {
            if (route.isEmpty()) continue;

            int prev = depotId;

            // depot → first → ... → last → depot
            for (int cityId : route) {
                totalCost += instance.getDistance(prev, cityId);
                prev = cityId;
            }

            totalCost += instance.getDistance(prev, depotId);
        }

        this.cost = totalCost;
        return totalCost;
    }

    public void printSolution() {
        for (int i = 0; i < routes.size(); i++) {
            System.out.print("Route #" + (i + 1) + ": ");
            for (int nodeId : routes.get(i)) {
                System.out.print(nodeId - 1 + " ");
            }
            System.out.println();
        }
        System.out.println("Cost " + cost);
    }


    public void printCost() {
        System.out.println("Cost " + cost);
        System.out.println();
    }


    public boolean isSolutionValid(Instance instance) {
        for (List<Integer> route : routes) {
            int currentCapacity = 0;
            for (Integer cityId : route)
                currentCapacity += instance.getCities().get(cityId - 1).getDemand(); // ids are 1-based

            if (currentCapacity > instance.getTruckCapacity())
                return false;
        }
        return true;
    }


}
