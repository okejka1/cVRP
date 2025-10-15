package model;

import java.util.List;

public class Solution {
    private List<List<Integer>> routes;
    private int cost;

    public Solution(List<List<Integer>> routes) {
        this.routes = routes;
        this.cost = Integer.MAX_VALUE;
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

        for (List<Integer> route : routes) {
            int depotId = instance.getDepotId();
            totalCost += instance.getDistance(depotId, route.getFirst());
            for (int i = 0; i < route.size(); i++) {
                int fromId = route.get(i);
                int toId = route.get(i + 1);
                totalCost += instance.getDistance(fromId, toId);
            }
            totalCost += instance.getDistance(route.getLast(), depotId);

        }

        this.cost = totalCost;
        return totalCost;
    }


    public void printSolution() {
        for (int i = 0; i < routes.size(); i++) {
            System.out.print("Route #" + (i + 1) + ": ");
            for (int nodeId : routes.get(i)) {
                System.out.print(nodeId + " ");
            }
            System.out.println();
        }
        System.out.println("Cost " + cost);
    }
}
