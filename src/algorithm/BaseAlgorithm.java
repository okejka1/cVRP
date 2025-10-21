package algorithm;

import model.Instance;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
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

    protected List<Integer> perfomSwapOnFlatList(List<Integer> sequence, SecureRandom secureRandom) {
        List<Integer> flatList = new ArrayList<>(sequence);
        int i, j;
        do {
            i = secureRandom.nextInt(sequence.size());
            j = secureRandom.nextInt(sequence.size());

        } while (i == j);

        Integer tmp = flatList.get(i);
        flatList.set(i, flatList.get(j));
        flatList.set(j, tmp);

        return flatList;

    }

    protected List<Integer> performInversionOnFlatList(List<Integer> sequence, SecureRandom secureRandom) {
        List<Integer> flatList = new ArrayList<>(sequence);
        int start = secureRandom.nextInt(flatList.size());
        int end = secureRandom.nextInt(flatList.size());
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }
        List<Integer> subList = flatList.subList(start, end + 1);
        Collections.reverse(subList);

        return flatList;

    }


}
