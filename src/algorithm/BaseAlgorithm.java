package algorithm;

import model.Instance;

public abstract class BaseAlgorithm implements IAlgorithm {
    protected final Instance instance;

    public BaseAlgorithm(Instance instance) {
        this.instance = instance;
    }

    public Instance getInstance() {
        return instance;
    }

}
