import algorithm.Genetic;
import algorithm.Greedy;
import algorithm.Random;
import algorithm.SimulatedAnnealing;
import io.Logger;
import model.Instance;
import model.Solution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Test:");
        Instance instance = Logger.loadFromFile("src/io/input/a-n32-k5.vrp");
//        instance.printDistanceMatrix(instance.getDistanceMatrix());
        Random random = new Random(instance);
//        Solution solution = random.runAlgorithm();
//        solution.printSolution();

//        Solution solution2 = random.runAlgorithm();
//        solution2.printSolution();

//        for(int i = 0; i < 10; i++) {
//            System.out.println("Iteration " + i + ":");
//            solution = random.runAlgorithm();
//            solution.printSolution();
//        }
//
        Greedy greedy = new Greedy(instance);
        Solution sol2 = greedy.runAlgorithm();
        sol2.printSolution();
        List<Solution> results = new ArrayList<>(10);
        for(int i = 0; i < 10; i++) {
//            Genetic genetic = new Genetic(instance, 500,0.7,0.2,0.05, 1000, 7);
//            Solution sol = genetic.runAlgorithm();
            SimulatedAnnealing simulatedAnnealing = new SimulatedAnnealing(instance, 10000, 1, 0.995, 200);
            Solution sol = simulatedAnnealing.runAlgorithm();
            results.add(sol);
        }

        int total = 0;
        for(Solution sol: results) {
            sol.printCost();
            total += sol.getCost();
        }
        int mean = total / results.size();

        System.out.println("Mean:" + mean);

    }

}
