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


    public Genetic(Instance instance, int populationSize, double crossoverFactor, double mutationFactor, double elitismFactor, int maxGenerations, int tour) {
        super(instance);
        this.populationSize = populationSize;
        this.crossoverFactor = crossoverFactor;
        this.mutationFactor = mutationFactor;
        this.elitismFactor = elitismFactor;
        this.maxGenerations = maxGenerations;
        this.tour = tour;
    }

    private Solution selectParent( List<Solution> currentGeneration) {

        Set<Solution> parentCandidates = new HashSet<>();
        while (parentCandidates.size() < Math.min(tour, currentGeneration.size())) {
            Solution candidate = currentGeneration.get(rand.nextInt(currentGeneration.size()));
            parentCandidates.add(candidate);
        }

        return Collections.max(parentCandidates, Comparator.comparing(Solution::getFitness));
    }

    // Gene mutation
    private Solution swapMutation(Solution solution, Instance instance) {

        Solution mutatedSolution = new Solution(solution); // deep copy
        List<List<Integer>> routes = mutatedSolution.getRoutes();

        int routeIndexA = rand.nextInt(routes.size());
        int routeIndexB = rand.nextInt(routes.size());

        int posA = rand.nextInt(routes.get(routeIndexA).size());
        int posB = rand.nextInt(routes.get(routeIndexB).size());

        Integer firstCustomerId = routes.get(routeIndexA).get(posA);
        Integer secondCustomerId = routes.get(routeIndexB).get(posB);

        routes.get(routeIndexA).set(posA, secondCustomerId);
        routes.get(routeIndexB).set(posB, firstCustomerId);

        mutatedSolution.setRoutes(routes);
        mutatedSolution.calculateCost(instance);
        mutatedSolution.calculateFitness();

        if (mutatedSolution.isSolutionValid(instance))
            return mutatedSolution;

        return innerRouteSwapMutation(solution, instance);
    }

    private Solution innerRouteSwapMutation(Solution solution, Instance instance) {
        Solution mutated = new Solution(solution); // deep copy
        List<List<Integer>> routes = mutated.getRoutes();

        int routeIndex = rand.nextInt(routes.size());
        List<Integer> route = routes.get(routeIndex);

        if (!(route.size() < 2)) {

            int i = rand.nextInt(route.size());
            int j = (i + 1 + rand.nextInt(route.size() - 1)) % route.size();
            Collections.swap(route, i, j);

            mutated.calculateCost(instance);
            mutated.calculateFitness();

            return mutated;
        }
        return solution;
    }




    private Solution OXCrossover(Solution parent1, Solution parent2, Instance instance) {

        // Flatten both parents (ignore depot)
        List<Integer> p1Flat = parent1.getRoutes().stream().flatMap(List::stream).toList();
        List<Integer> p2Flat = parent2.getRoutes().stream().flatMap(List::stream).toList();
        int size = p1Flat.size();

        // Random segment boundaries
        int start = rand.nextInt(size);
        int end = rand.nextInt(size);

        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }

        List<Integer> flattenOffspring = new ArrayList<>(Collections.nCopies(size, -1));

        Set<Integer> used = new HashSet<>();
        for (int i = start; i <= end; i++) {
            flattenOffspring.set(i, p1Flat.get(i));
            used.add(p1Flat.get(i));
        }

        int currentIndex = (end + 1) % size;
        for (int i = 0; i < size; i++) {
            int candidateId = p2Flat.get((end + 1 + i) % size);
            if (!used.contains(candidateId)) {
                flattenOffspring.set(currentIndex, candidateId);
                used.add(candidateId);
                currentIndex = (currentIndex + 1) % size;
            }
        }

        List<List<Integer>> newRoutes = greedySplit(flattenOffspring, instance);

        Solution offspring = new Solution(parent1);
        offspring.setRoutes(newRoutes);
        offspring.calculateCost(instance);
        offspring.calculateFitness();

        return offspring;
    }

    private Solution routeBasedCrossover(Solution parent1, Solution parent2, Instance instance) {
        List<List<Integer>> p1Routes = parent1.getRoutes();
        List<List<Integer>> childRoutes = new ArrayList<>();

        Set<Integer> used = new HashSet<>();

        for (List<Integer> route : p1Routes) {
            if (rand.nextDouble() < 0.4) {
                childRoutes.add(new ArrayList<>(route));
                used.addAll(route);
            }
        }

        List<Integer> remainingCustomers = parent2.getRoutes().stream()
                .flatMap(List::stream)
                .filter(c -> !used.contains(c))
                .toList();

        List<List<Integer>> newRoutes = greedySplit(remainingCustomers, instance);

        childRoutes.addAll(newRoutes);

        Solution offspring = new Solution(parent1); // inherit structure
        offspring.setRoutes(childRoutes);
        offspring.calculateCost(instance);
        offspring.calculateFitness();

        return offspring;
    }



    private List<List<Integer>> greedySplit(List<Integer> sequence, Instance instance) {
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

        // Add the last route
        if (!currentRoute.isEmpty()) {
            routes.add(currentRoute);
        }

        return routes;
    }


    @Override
    public Solution runAlgorithm() {
        // init
        Solution bestSolution;
        List<Solution> currentGeneration = new ArrayList<>(populationSize);

        for (int i = 0; i < populationSize; i++) {
            Solution solution;
            if (i == 0) {
                Greedy greedy = new Greedy(instance);
                solution = greedy.runAlgorithm();
            } else {
                Random random = new Random(instance);
                solution = random.runAlgorithm();
            }
            currentGeneration.add(solution);
        }
        // sort
        currentGeneration = currentGeneration.stream()
                .sorted(Comparator.comparingDouble(Solution::getFitness).reversed()).toList();

        bestSolution = currentGeneration.getFirst();

        int counterBestSolutionPlateu = 0;

        for (int currentGenerationCounter = 0; currentGenerationCounter < maxGenerations; currentGenerationCounter++) {
            int elitismSolutionsCounter = (int) Math.round(elitismFactor * populationSize); // number of Solutions to be taken to next generation
            List<Solution> newGeneration = new ArrayList<>(currentGeneration.subList(0, elitismSolutionsCounter));
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

                // Mutation
                if (Math.random() < mutationFactor) {
                    child = swapMutation(child, instance);
                }

                if (uniqueSolutions.add(child)) {
                    newGeneration.add(child);
                }
            }

            // --- Sort next generation ---
            newGeneration.sort(Comparator.comparingDouble(Solution::getFitness).reversed());

            // --- Update best solution ---
            if (newGeneration.getFirst().getFitness() > bestSolution.getFitness()) {
                bestSolution = newGeneration.getFirst();
                counterBestSolutionPlateu = 0;

            }
            counterBestSolutionPlateu++;
            // Replace old population
            currentGeneration = newGeneration;
            if(counterBestSolutionPlateu > 60) {
                System.out.println("Have not improved since: " + counterBestSolutionPlateu + " generations");
                bestSolution.printCost();
            }

            System.out.println("Current generation:" + currentGenerationCounter);

        }

        return bestSolution;
    }
}
