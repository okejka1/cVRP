package algorithm;

import model.Instance;
import model.Solution;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class SimulatedAnnealing extends BaseAlgorithm {

    private final double maxTemp;
    private final double minTemp;
    private final double coolingRate;
    private final int neighbourCheckCount;

    private final SecureRandom rand = new SecureRandom();

    public SimulatedAnnealing(Instance instance, double maxTemp, double minTemp, double coolingRate, int neighbourCheckCount) {
        super(instance);
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
        this.coolingRate = coolingRate;
        this.neighbourCheckCount = neighbourCheckCount;
    }

    @Override
    public Solution runAlgorithm() {

        Greedy greedy = new Greedy(instance);
        Solution currentSolution = greedy.runAlgorithm();
        Solution bestSolution = new Solution(currentSolution);

        double currentTemp = maxTemp;

        while (currentTemp > minTemp) {
            for (int iter = 0; iter < neighbourCheckCount; iter++) { // number of neighbors per temperature
                Solution neighbor = generateNeighbor(currentSolution);
                double delta = neighbor.getCost() - currentSolution.getCost();

                if (delta < 0) {
                    currentSolution = neighbor; // accept better solution
                } else {
                    double prob = Math.exp(-delta / currentTemp); // accept worse solution with probability
                    if (Math.random() < prob) {
                        currentSolution = neighbor;
                    }
                }

                if (currentSolution.getCost() < bestSolution.getCost()) {
                    bestSolution = new Solution(currentSolution);
                }
            }

            currentTemp *= coolingRate; // cool down
        }
        return bestSolution;
    }

    private Solution generateNeighbor(Solution solution) {
        Solution neighbor = new Solution(solution);
        List<Integer> flatList = new ArrayList<>(neighbor.getRoutes().stream().flatMap(List::stream).toList());

        if(rand.nextBoolean())
            flatList = perfomSwapOnFlatList(flatList, rand);
        else
            flatList = performInversionOnFlatList(flatList, rand);

        neighbor.setRoutes(greedySplit(flatList, instance));
        neighbor.calculateCost(instance);

        return neighbor;
    }

}
