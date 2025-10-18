import algorithm.Greedy;
import algorithm.Random;
import io.Logger;
import model.Instance;
import model.Solution;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Test:");
        Instance instance = Logger.loadFromFile("src/io/input/toy.vrp");
        instance.printDistanceMatrix(instance.getDistanceMatrix());
        Random random = new Random(instance);
        Solution solution = random.runAlgorithm();
        solution.printSolution();
//        Solution solution2 = random.runAlgorithm();
//        solution2.printSolution();

        for(int i = 0; i < 10; i++) {
            System.out.println("Iteration " + i + ":");
            solution = random.runAlgorithm();
            solution.printSolution();
        }
//
//        Greedy greedy = new Greedy(instance);
//        Solution sol2 = greedy.runAlgorithm();
//        sol2.printSolution();


    }
}
