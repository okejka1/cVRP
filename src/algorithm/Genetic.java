package algorithm;

import model.Instance;
import model.Solution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Genetic extends BaseAlgorithm {

    private int populationSize;
    private int maxGenerations;
    private double mutationFactor;
    private double crossoverFactor;


    public Genetic(Instance instance, int populationSize, double crossoverFactor, double mutationFactor, int maxGenerations) {
        super(instance);
        this.populationSize = populationSize;
        this.crossoverFactor = crossoverFactor;
        this.mutationFactor = mutationFactor;
        this.maxGenerations = maxGenerations;
    }

    private Solution selectParent(int tourParam) {

        Solution bestParent;







        return null;
    }



    @Override
    public Solution runAlgorithm() {
    // init
        Solution bestSolution;
        List<Solution> population = new ArrayList<>(populationSize);

        for (int i = 0; i < populationSize; i++) {
            Solution solution;
            if (i == 0) {
                Greedy greedy = new Greedy(instance);
                solution = greedy.runAlgorithm();
            } else {
                Random random = new Random(instance);
                solution = random.runAlgorithm();
            }
            population.add(solution);
        }
        // sort
        population = population.stream()
                .sorted(Comparator.comparingDouble(Solution::getFitness).reversed()).toList();

        bestSolution = population.getFirst();





        for (Solution sol: population) {
            sol.printCost();
        }

        System.out.println("Best solution: ");
        bestSolution.printCost();

        return null;
    }
}
