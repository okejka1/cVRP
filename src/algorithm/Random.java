package algorithm;

import model.Instance;
import model.Node;
import model.Solution;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Random extends BaseAlgorithm {

    public Random(Instance instance) {
        super(instance);
    }

    @Override
    public Solution runAlgorithm() {
        SecureRandom rand = new SecureRandom();

        List<Node> customers = new ArrayList<>(instance.getCities());
        customers.removeIf(n -> n.getId() == instance.getDepotId());

        Collections.shuffle(customers, rand);


        List<List<Integer>> routes = new ArrayList<>();
        List<Integer> currentRoute = new ArrayList<>();
        int currentLoad = 0;

        for (Node customer : customers) {
            if (currentLoad + customer.getDemand() > instance.getTruckCapacity()) {
                routes.add(new ArrayList<>(currentRoute));
                currentRoute.clear();
                currentLoad = 0;
            }

            currentRoute.add(customer.getId());
            currentLoad += customer.getDemand();
        }

        if (!currentRoute.isEmpty()) {
            routes.add(new ArrayList<>(currentRoute));
        }

        return new Solution(routes, instance);  // calculates cost during initialization
    }
}
