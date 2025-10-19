import algorithm.Genetic;
import algorithm.Greedy;
import algorithm.Random;
import io.Logger;
import model.Instance;
import model.Solution;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

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

        Genetic genetic = new Genetic(instance, 500,0.7,0.1,0.1, 1000, 20);
        Solution sol = genetic.runAlgorithm();
        sol.printCost();
        sol.printSolution();

    }
}
