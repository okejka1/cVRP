import algorithm.*;
import io.Logger;
import model.Instance;
import model.ResultSummary;
import model.Solution;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Test:");


        String directory = "src/io/input/";
        List<Instance> instances = Logger.loadAllFromDirectory(directory);

        Instance instance = instances.get(0);
        ResultSummary randomRes = testInstance(instance, AlgorithmType.RANDOM);
        ResultSummary greedyRes = testInstance(instance, AlgorithmType.GREEDY);
        ResultSummary saRes = testInstance(instance, AlgorithmType.SA);
        ResultSummary gaRes = testInstance(instance, AlgorithmType.GA);

        // Print summary
        System.out.println(randomRes);
        System.out.println(greedyRes);
        System.out.println(saRes);
        System.out.println(gaRes);

    }

    private static ResultSummary testInstance(Instance instance, AlgorithmType algorithmType) {
        List<Solution> solutions = new ArrayList<>();
        switch (algorithmType) {
            case RANDOM:
                for (int i = 0; i < 10000; i++) {
                    Random random = new Random(instance);
                    Solution solution = random.runAlgorithm();
                    solutions.add(solution);
                }
                break;
            case GREEDY:
                for (int i = 0; i < instance.getCities().size() - 1; i++) {
                    Greedy greedy = new Greedy(instance, i + 1);
                    Solution solution = greedy.runAlgorithm();
                    solutions.add(solution);
                }
                break;
            case SA:
                for (int i = 0; i < 10; i++) {
                    SimulatedAnnealing sa = new SimulatedAnnealing(instance, 10000, 1, 0.995, 200);
                    Solution solution = sa.runAlgorithm();
                    solutions.add(solution);
                }
                break;
            case GA:
                for (int i = 0; i < 10; i++) {
                    Genetic genetic = new Genetic(instance, 200, 0.9, 0.5, 0.1, 10000,6);
                    Solution solution = genetic.runAlgorithm();
                    solutions.add(solution);
                }
                break;
        }

        // Compute statistics
        double best = solutions.stream().mapToDouble(Solution::getCost).min().orElse(Double.NaN);
        double worst = solutions.stream().mapToDouble(Solution::getCost).max().orElse(Double.NaN);
        double avg = solutions.stream().mapToDouble(Solution::getCost).average().orElse(Double.NaN);
        double std = Math.sqrt(solutions.stream()
                .mapToDouble(s -> Math.pow(s.getCost() - avg, 2))
                .sum() / solutions.size());

        String algoName = algorithmType.name();
        return new ResultSummary(instance.getName(), algoName, best, worst, avg, std, solutions);
    }

}
