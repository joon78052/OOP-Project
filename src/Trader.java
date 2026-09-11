import java.util.Random;

public abstract class Trader {

    protected String name;
    protected double cash;

    public Trader(String name, double cash) {
        this.name = name;
        this.cash = cash;
    }

    public String getName() {
        return name;
    }

    public double getCash() {
        return cash;
    }

    public abstract Order makeDecision(
            Stock stock,
            Random random
    );
}