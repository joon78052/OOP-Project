import java.util.Random;

public abstract class Trader {

    protected String name;
    protected double cash;
    private TradingStrategy strategy;

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

    public TradingStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(TradingStrategy strategy) {
        this.strategy = strategy;
    }

    public abstract Order makeDecision(
            Stock stock,
            Random random
    );
}