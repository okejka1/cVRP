package algorithm;

import io.Logger;
import model.Instance;
import model.Solution;

import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.*;

public class Genetic extends BaseAlgorithm {

    private int populationSize;
    private int maxGenerations;
    private double mutationFactor;
    private double crossoverFactor;
    private double elitismFactor;
    private int tour;
    private ConfigRunnerType configRunnerType;

    private final SecureRandom rand = new SecureRandom();

    public Genetic(Instance instance, ConfigRunnerType configRunnerType, int populationSize, double crossoverFactor, double mutationFactor,
                   double elitismFactor, int maxGenerations, int tour) {
        super(instance);
        this.configRunnerType = configRunnerType;
        this.populationSize = populationSize;
        this.crossoverFactor = crossoverFactor;
        this.mutationFactor = mutationFactor;
        this.elitismFactor = elitismFactor;
        this.maxGenerations = maxGenerations;
        this.tour = tour;

    }


    private Solution selectParent(List<Solution> currentGeneration) {
        Set<Solution> parentCandidates = new HashSet<>();
        while (parentCandidates.size() < Math.min(tour, currentGeneration.size())) {
            Solution candidate = currentGeneration.get(rand.nextInt(currentGeneration.size()));
            parentCandidates.add(candidate);
        }
        return Collections.min(parentCandidates, Comparator.comparing(Solution::getCost));
    }


    private Solution mutate(Solution solution, Instance instance) {
        Solution mutated = new Solution(solution);
        List<Integer> flatList = new ArrayList<>(mutated.getRoutes().stream().flatMap(List::stream).toList());

        if (rand.nextBoolean())
            // swap mutation
            flatList = perfomSwapOnFlatList(flatList, rand);
        else
            // inversion mutation
            flatList = performInversionOnFlatList(flatList, rand);

        List<List<Integer>> newRoutes = greedySplit(flatList, instance);
        mutated.setRoutes(newRoutes);
        mutated.calculateCost(instance);
//        mutated.calculateFitness();
        return mutated;
    }

    private Solution OXCrossover(Solution parent1, Solution parent2, Instance instance) {
        List<Integer> p1 = parent1.getRoutes().stream().flatMap(List::stream).toList();
        List<Integer> p2 = parent2.getRoutes().stream().flatMap(List::stream).toList();
        int size = p1.size();

        int start = rand.nextInt(size);
        int end = rand.nextInt(size);
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }

        List<Integer> childSeq = new ArrayList<>(Collections.nCopies(size, -1));
        Set<Integer> used = new HashSet<>();

        // Copy segment from parent1
        for (int i = start; i <= end; i++) {
            childSeq.set(i, p1.get(i));
            used.add(p1.get(i));
        }

        // Fill remaining positions from parent2
        int idx = (end + 1) % size;
        for (int i = 0; i < size; i++) {
            int gene = p2.get((end + 1 + i) % size);
            if (!used.contains(gene)) {
                childSeq.set(idx, gene);
                used.add(gene);
                idx = (idx + 1) % size;
            }
        }

        List<List<Integer>> newRoutes = greedySplit(childSeq, instance);
        Solution child = new Solution(parent1);
        child.setRoutes(newRoutes);
        child.calculateCost(instance);
//        child.calculateFitness();
        return child;
    }


    @Override
    public Solution runAlgorithm() {
        FileWriter evalWriter = null;
        if (this.configRunnerType == ConfigRunnerType.EVALUATION_FILE) {
            evalWriter = Logger.createEvaluationFile(instance.getName(), AlgorithmType.GA);
        }

        List<Solution> currentGeneration = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            Solution solution;
            if (i == 0) {
                Greedy greedy = new Greedy(instance);
                solution = greedy.runAlgorithm();
            } else {
                Random randomAlg = new Random(instance);
                solution = randomAlg.runAlgorithm();
            }
            currentGeneration.add(solution);
        }

        currentGeneration.sort(Comparator.comparingInt(Solution::getCost));
        Solution bestSolution = currentGeneration.get(0);

        double best = currentGeneration.getFirst().getCost();
        double worst = currentGeneration.getLast().getCost();
        double avg = currentGeneration.stream().mapToDouble(Solution::getCost).average().getAsDouble();


        if (evalWriter != null && configRunnerType.equals(ConfigRunnerType.EVALUATION_FILE)) {
            try {
                Logger.logGeneration(evalWriter, 0, best, avg, worst);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        int counterBestSolutionPlateau = 0;


        for (int generation = 0; generation < maxGenerations; generation++) {
            int elitismCount = (int) Math.round(elitismFactor * populationSize);
            List<Solution> newGeneration = new ArrayList<>(currentGeneration.subList(0, elitismCount));
            Set<Solution> uniqueSolutions = new HashSet<>(newGeneration);

            while (newGeneration.size() < populationSize) {
                Solution parent1 = selectParent(currentGeneration);
                Solution parent2 = selectParent(currentGeneration);

                Solution child;
                if (Math.random() < crossoverFactor) {
                    child = OXCrossover(parent1, parent2, instance);
                } else {
                    child = new Solution(parent1);
                }

                if (Math.random() < mutationFactor) {
                    child = mutate(child, instance);
                }

                if (uniqueSolutions.add(child)) {
                    newGeneration.add(child);
                }
            }

//            // --- Inject Random Diversity ---
//            if (generation % 2000 == 0) {
//                for (int i = 0; i < populationSize / 10; i++) {
//                    Random randomAlg = new Random(instance);
//                    Solution randomSolution = randomAlg.runAlgorithm();
//                    newGeneration.add(randomSolution);
//                }
//            }

            newGeneration.sort(Comparator.comparingInt(Solution::getCost));

            if (newGeneration.getFirst().getCost() < bestSolution.getCost())
                bestSolution = newGeneration.getFirst();
//                counterBestSolutionPlateau = 0;
//            } else {
//              counterBestSolutionPlateau++;
//            }

            if (evalWriter != null && configRunnerType.equals(ConfigRunnerType.EVALUATION_FILE)) {
                try {
                    best = newGeneration.getFirst().getCost();
                    worst = newGeneration.getLast().getCost();
                    avg = newGeneration.stream().mapToDouble(Solution::getCost).average().getAsDouble();
                    Logger.logGeneration(evalWriter, generation + 1, best, avg, worst);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            currentGeneration = newGeneration;

//            if (counterBestSolutionPlateau > 60) {
//                System.out.println("No improvement for " + counterBestSolutionPlateau + " generations");
//                bestSolution.printCost();
//            }
//
            System.out.println("Generation: " + generation + " Best Cost: " + bestSolution.getCost());

        }
        try {
            if (evalWriter != null) evalWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bestSolution;
    }
}
