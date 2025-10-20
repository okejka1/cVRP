package model;

import java.util.List;

public class ResultSummary {
    private final String instanceName;
    private final String algorithmName;
    private final double best;
    private final double worst;
    private final double avg;
    private final double std;
    private final List<Solution> solutions;

    public ResultSummary(String instanceName, String algorithmName,
                         double best, double worst, double avg, double std,
                         List<Solution> solutions) {
        this.instanceName = instanceName;
        this.algorithmName = algorithmName;
        this.best = best;
        this.worst = worst;
        this.avg = avg;
        this.std = std;
        this.solutions = solutions;
    }

    public String getInstanceName() { return instanceName; }
    public String getAlgorithmName() { return algorithmName; }
    public double getBest() { return best; }
    public double getWorst() { return worst; }
    public double getAvg() { return avg; }
    public double getStd() { return std; }
    public List<Solution> getSolutions() { return solutions; }

    @Override
    public String toString() {
        return String.format("[%s - %s] best=%.2f, worst=%.2f, avg=%.2f, std=%.2f",
                instanceName, algorithmName, best, worst, avg, std);
    }
}
