public abstract class Trader {

    protected String name;
    protected double cash;

    public Trader(String name, double cash) {
        this.name = name;
        this.cash = cash;
    }

    public abstract void makeDecision();
}
