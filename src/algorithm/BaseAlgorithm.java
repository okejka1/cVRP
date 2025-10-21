package algorithm;

import model.Instance;
import model.Solution;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseAlgorithm implements IAlgorithm {
    protected final Instance instance;

    public BaseAlgorithm(Instance instance) {
        this.instance = instance;
    }

    protected List<List<Integer>> greedySplit(List<Integer> sequence, Instance instance) {
        List<List<Integer>> routes = new ArrayList<>();
        List<Integer> currentRoute = new ArrayList<>();
        int currentLoad = 0;

        for (int customerId : sequence) {
            int demand = instance.getCities().get(customerId - 1).getDemand(); // IDs are 1-based

            if (currentLoad + demand <= instance.getTruckCapacity()) {
                currentRoute.add(customerId);
                currentLoad += demand;
            } else {
                routes.add(new ArrayList<>(currentRoute));
                currentRoute.clear();
                currentRoute.add(customerId);
                currentLoad = demand;
            }
        }

        if (!currentRoute.isEmpty()) {
            routes.add(currentRoute);
        }

        return routes;
    }

    protected Solution generateNeighbor(Solution solution) {
        Solution neighbor = new Solution(solution);

        List<Integer> flatList = new ArrayList<>(neighbor.getRoutes().stream().flatMap(List::stream).toList());


        int i = (int) (Math.random() * flatList.size());
        int j = (int) (Math.random() * flatList.size());

        // Swap
        int temp = flatList.get(i);
        flatList.set(i, flatList.get(j));
        flatList.set(j, temp);

        // Split back into routes respecting truck capacity
        neighbor.setRoutes(greedySplit(flatList, instance));
        neighbor.calculateCost(instance);
        neighbor.calculateFitness();

        return neighbor;
    }


}
