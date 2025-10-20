package algorithm;

import model.Instance;
import model.Solution;

import java.security.SecureRandom;
import java.util.*;

public class Genetic extends BaseAlgorithm {

    private int populationSize;
    private int maxGenerations;
    private double mutationFactor;
    private double crossoverFactor;
    private double elitismFactor;
    private int tour;

    private final SecureRandom rand = new SecureRandom();

    public Genetic(Instance instance, int populationSize, double crossoverFactor, double mutationFactor,
                   double elitismFactor, int maxGenerations, int tour) {
        super(instance);
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


    private Solution enhancedMutation(Solution solution, Instance instance) {
        Solution mutated = new Solution(solution); // deep copy
        List<Integer> flatList = new ArrayList<>(mutated.getRoutes().stream().flatMap(List::stream).toList());

        if (rand.nextBoolean()) {
            // Swap two positions
            int i = rand.nextInt(flatList.size());
            int j = rand.nextInt(flatList.size());
            int tmp = flatList.get(i);
            flatList.set(i, flatList.get(j));
            flatList.set(j, tmp);
        } else {
            // Invert a segment
            int start = rand.nextInt(flatList.size());
            int end = rand.nextInt(flatList.size());
            if (start > end) { int tmp = start; start = end; end = tmp; }
            List<Integer> subList = flatList.subList(start, end + 1);
            Collections.reverse(subList);
        }

        List<List<Integer>> newRoutes = greedySplit(flatList, instance);
        mutated.setRoutes(newRoutes);
        mutated.calculateCost(instance);
        mutated.calculateFitness();
        return mutated;
    }

    private Solution OXCrossover(Solution parent1, Solution parent2, Instance instance) {
        List<Integer> p1 = parent1.getRoutes().stream().flatMap(List::stream).toList();
        List<Integer> p2 = parent2.getRoutes().stream().flatMap(List::stream).toList();
        int size = p1.size();

        int start = rand.nextInt(size);
        int end = rand.nextInt(size);
        if (start > end) { int tmp = start; start = end; end = tmp; }

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
        child.calculateFitness();
        return child;
    }


    @Override
    public Solution runAlgorithm() {
        // --- Initialize Population ---
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

        int counterBestSolutionPlateau = 0;

        // --- Main GA Loop ---
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
                    child = enhancedMutation(child, instance);
                }

                if (uniqueSolutions.add(child)) {
                    newGeneration.add(child);
                }
            }

//            // --- Inject Random Diversity ---
//            if (generation % 50 == 0) {
//                for (int i = 0; i < populationSize / 10; i++) {
//                    Random randomAlg = new Random(instance);
//                    Solution randomSolution = randomAlg.runAlgorithm();
//                    newGeneration.add(randomSolution);
//                }
//            }

            newGeneration.sort(Comparator.comparingInt(Solution::getCost));

            if (newGeneration.getFirst().getCost() < bestSolution.getCost()) {
                bestSolution = newGeneration.getFirst();
                counterBestSolutionPlateau = 0;
            } else {
                counterBestSolutionPlateau++;
            }

            currentGeneration = newGeneration;

            if (counterBestSolutionPlateau > 60) {
                System.out.println("No improvement for " + counterBestSolutionPlateau + " generations");
                bestSolution.printCost();
            }

            System.out.println("Generation: " + generation + " Best Cost: " + bestSolution.getCost());
        }

        return bestSolution;
    }
}
