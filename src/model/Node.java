package model;

public class Node {
    private int id;
    private int x;
    private int y;
    private int demand;

    public Node(int id, int x, int y, int demand) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.demand = demand;
    }

    public int getId() {
        return id;
    }


    public int getX() {
        return x;
    }


    public int getY() {
        return y;
    }


    public int getDemand() {
        return demand;
    }

    public void setDemand(int demand) {
        this.demand = demand;
    }
}
