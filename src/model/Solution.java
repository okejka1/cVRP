package model;

import java.util.List;

public class Solution {
    private List<List<Integer>> routes;
    private int cost;
    private double fitness;



    public Solution(List<List<Integer>> routes, Instance instance) {
        this.routes = routes;
        this.cost = calculateCost(instance);
        this.fitness = calculateFitness();
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

    public double calculateFitness() {
        fitness = 1.0 / cost;
        return fitness;
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

    public double getFitness() {
        return fitness;
    }

    public void printCost() {
        System.out.println("Cost " + cost);
        System.out.println("Fitness " + fitness);
        System.out.println();
    }


}
