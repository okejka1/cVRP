package utils;

public class Config {
    private boolean shouldPrintBestSolution;
    private boolean shouldSave;
    private double mutationRatio;

    public double getMutationRatio() {
        return mutationRatio;
    }

    public void setMutationRatio(double mutationRatio) {
        this.mutationRatio = mutationRatio;
    }

    public Config(boolean shouldPrintBestSolution, boolean shouldSave, double mutationRatio) {
        this.shouldPrintBestSolution = shouldPrintBestSolution;
        this.shouldSave = shouldSave;
        this.mutationRatio = mutationRatio;
    }

    public boolean isShouldPrintBestSolution() {
        return shouldPrintBestSolution;
    }

    public void setShouldPrintBestSolution(boolean shouldPrintBestSolution) {
        this.shouldPrintBestSolution = shouldPrintBestSolution;
    }

    public boolean isShouldSave() {
        return shouldSave;
    }

    public void setShouldSave(boolean shouldSave) {
        this.shouldSave = shouldSave;
    }

    public Config(boolean shouldPrintBestSolution, boolean shouldSave) {
        this.shouldPrintBestSolution = shouldPrintBestSolution;
        this.shouldSave = shouldSave;
    }
}
