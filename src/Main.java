import algorithm.*;
import io.Logger;
import model.Instance;
import model.ResultSummary;
import model.Solution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        final String directory = "src/io/input/";
        List<Instance> instances = List.of();
        try {
            instances = Logger.loadAllFromDirectory(directory);
        } catch (IOException e) {
            e.printStackTrace();
        }


        for (Instance instance : instances) {
            ResultSummary randomRes = testInstance(instance, AlgorithmType.RANDOM);
            ResultSummary greedyRes = testInstance(instance, AlgorithmType.GREEDY);
            ResultSummary saRes = testInstance(instance, AlgorithmType.SA);
            ResultSummary gaRes = testInstance(instance, AlgorithmType.GA);

            List<ResultSummary> allSummaries = List.of(randomRes, greedyRes, saRes, gaRes);

            try {
                Logger.saveInstanceResultsToCSV(instance.getName(), allSummaries);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
                    Genetic genetic = new Genetic(instance, ConfigRunnerType.EVALUATION_FILE, 500, 0.8, 0.3, 0.05, 10000, 20);
                    Solution solution = genetic.runAlgorithm();
                    solutions.add(solution);
                }
                break;
        }

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