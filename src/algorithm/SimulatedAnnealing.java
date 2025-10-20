package algorithm;

import model.Instance;
import model.Solution;

import java.util.ArrayList;
import java.util.List;

public class SimulatedAnnealing extends BaseAlgorithm {

    private double maxTemp;
    private double minTemp;
    private double coolingRate;
    private int neighbourCheckCount;

    public SimulatedAnnealing(Instance instance, double maxTemp, double minTemp, double coolingRate, int neighbourCheckCount) {
        super(instance);
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
        this.coolingRate = coolingRate;
        this.neighbourCheckCount = neighbourCheckCount;
    }

    @Override
    public Solution runAlgorithm() {

//        1. Initialize:
//        - Cooling schedule, maximum and minimum temperatures (Tmax, Tmin)
//                - Generate an initial solution s0
//        - Set the current solution s = s0
//                - Set the starting temperature T = Tmax
//
//        2. Repeat until T <= Tmin:
//        a. Repeat until a condition is met at each temperature:
//        - Generate a neighboring solution s'
//                - Calculate the change in cost (∆E) between s' and s
//                - If ∆E ≤ 0, accept the new solution s' as the current solution s
//                - Otherwise, accept s' with a probability proportional to e^(-∆E/T)
//        b. Decrease the temperature T = α * T (cooling step)
//
//        3. Output the best solution found

        // Start from a greedy solution
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
                    // Accept worse solution with probability
                    double prob = Math.exp(-delta / currentTemp);
                    if (Math.random() < prob) {
                        currentSolution = neighbor;
                    }
                }

                if (currentSolution.getCost() < bestSolution.getCost()) {
                    bestSolution = new Solution(currentSolution);
                }
            }

            // Cool down
            currentTemp *= coolingRate;
        }
        return bestSolution;
    }

}
