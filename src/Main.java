import io.Logger;
import model.Instance;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Test:");
        Instance instance = Logger.loadFromFile("src/io/input/toy.vrp");
        instance.printDistanceMatrix(instance.getDistanceMatrix());
    }
}
