import java.util.Random;

public abstract class Trader implements MarketEventListener {

    protected String name;
    protected double cash;
    private TradingStrategy strategy;
    private double sentiment;
    private int activeRecessions;

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

    public double getSentiment() {
        return sentiment;
    }

    @Override
    public void onMarketEvent(MarketEvent event) {
        if (event.getType() != MarketEventType.ECONOMIC_RECESSION) {
            return;
        }
        if (event.getPhase() == MarketEventPhase.ACTIVE) {
            activeRecessions++;
        } else if (event.getPhase() == MarketEventPhase.EXPIRED) {
            activeRecessions = Math.max(0, activeRecessions - 1);
        }
        sentiment = activeRecessions > 0 ? event.getSentimentDelta() : 0;
    }

    public Order makeDecision(Stock stock, Random random) {
        if (strategy == null) {
            return null;
        }
        return strategy.decide(this, stock, random);
    }
}