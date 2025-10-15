package utils;

import model.Node;

import java.util.List;

public class CommonUtils {

    public static int euclideanDistance(Node a, Node b) {
        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        return (int) Math.round(Math.sqrt(dx * dx + dy * dy));
    }

    public static int[][] buildDistanceMatrix(List<Node> cities) {
        int n = cities.size();
        int[][] dist = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                int d = (i == j) ? -1 : euclideanDistance(cities.get(i), cities.get(j));
                dist[i][j] = dist[j][i] = d;
            }
        }
        return dist;
    }


}
