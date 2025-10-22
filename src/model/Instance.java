package model;


import utils.CommonUtils;

import java.util.List;

public class Instance {
    private final String name;
    private final int truckCapacity;
    private final List<Node> cities;
    private final int[][] distanceMatrix;
    private final int depotId;

    public int[][] getDistanceMatrix() {
        return distanceMatrix;
    }

    public Instance(String name, int truckCapacity, List<Node> cities, int depotId) {
        this.name = name;
        this.truckCapacity = truckCapacity;
        this.cities = cities;
        this.distanceMatrix = CommonUtils.buildDistanceMatrix(cities);
        this.depotId = depotId;
    }

    public int getDepotId() {
        return depotId;
    }

    public List<Node> getCities() {
        return cities;
    }

    public int getTruckCapacity() {
        return truckCapacity;
    }

    public String getName() {
        return name;
    }

    public int getDistance(int fromId, int toId) {
        return distanceMatrix[fromId - 1][toId - 1];  // IDs are 1-based in .vrp files
    }

    public void printDistanceMatrix(int[][] distanceMatrix) {
        for (int i = 0; i < cities.size(); i++) {
            for (int j = 0; j < cities.size(); j++) {
                System.out.print(distanceMatrix[i][j] + " ");
            }
            System.out.println();
        }
    }


}
