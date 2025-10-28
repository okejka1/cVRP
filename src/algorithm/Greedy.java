package algorithm;

import model.Instance;
import model.Node;
import model.Solution;
import utils.Config;

import java.util.*;

public class Greedy extends BaseAlgorithm {

    private Integer firstCustomerId;

    public Greedy(Instance instance, Integer firstCustomerId) {
        super(instance);
        this.firstCustomerId = firstCustomerId;
    }

    public Greedy(Instance instance) {
        super(instance);
    }

    @Override
    public Solution runAlgorithm(Config config) {

        List<Node> customers = new ArrayList<>(instance.getCities());
        customers.removeIf(n -> n.getId() == instance.getDepotId());

        Set<Integer> unvisited = new HashSet<>();
        for (Node c : customers) unvisited.add(c.getId());

        List<List<Integer>> routes = new ArrayList<>();

        while (!unvisited.isEmpty()) {
            List<Integer> route = new ArrayList<>();
            int currentLoad = 0;
            int currentCity = instance.getDepotId();

            int nextCustomerId;
            if (firstCustomerId != null && unvisited.contains(firstCustomerId)) {
                nextCustomerId = firstCustomerId;
            } else {
                nextCustomerId = findNearestFeasible(currentCity, unvisited, customers, instance, currentLoad);
            }

            if (nextCustomerId == -1) break;

            route.add(nextCustomerId);
            currentLoad += getDemand(customers, nextCustomerId);
            unvisited.remove(nextCustomerId);
            currentCity = nextCustomerId;


            while (true) {
                nextCustomerId = findNearestFeasible(currentCity, unvisited, customers, instance, currentLoad);
                if (nextCustomerId == -1) break;

                route.add(nextCustomerId);
                currentLoad += getDemand(customers, nextCustomerId);
                unvisited.remove(nextCustomerId);
                currentCity = nextCustomerId;
            }

            routes.add(route);
        }

        return new Solution(routes, instance);  // calculates cost during initialization
    }


    private int findNearestFeasible(int fromId, Set<Integer> unvisited, List<Node> customers,
                                    Instance instance, int currentLoad) {
        int bestId = -1;
        int bestDist = Integer.MAX_VALUE;

        for (Node c : customers) {
            if (!unvisited.contains(c.getId())) continue;
            if (currentLoad + c.getDemand() > instance.getTruckCapacity()) continue;

            int d = instance.getDistance(fromId, c.getId());
            if (d < bestDist) {
                bestDist = d;
                bestId = c.getId();
            }
        }

        return bestId;
    }


    private int getDemand(List<Node> customers, int id) {
        for (Node n : customers) {
            if (n.getId() == id) return n.getDemand();
        }
        return 0;
    }
}
