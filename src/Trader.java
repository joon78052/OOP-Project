import java.util.Random;

public abstract class Trader implements MarketEventListener {

    private static final double EPSILON = 0.0000001;

    protected String name;
    protected double cash;
    private double reservedCash;
    private final Portfolio portfolio;
    private TradingStrategy strategy;
    private TradingStrategy normalStrategy;
    private TradingStrategy recessionStrategy;
    private double sentiment;
    private int activeRecessions;

    public Trader(String name, double cash) {
        if (cash < 0 || Double.isNaN(cash) || Double.isInfinite(cash)) {
            throw new IllegalArgumentException("Cash must be non-negative and finite");
        }
        this.name = name;
        this.cash = cash;
        portfolio = new Portfolio();
    }

    public String getName() {
        return name;
    }

    public double getCash() {
        return cash;
    }

    public double getReservedCash() {
        return reservedCash;
    }

    public double getAvailableCash() {
        return Math.max(0, cash - reservedCash);
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public void reserveCash(double amount) throws OrderException {
        requirePositive(amount, "Cash reservation");
        if (amount > getAvailableCash() + EPSILON) {
            throw new OrderException("Not enough available cash");
        }
        reservedCash += amount;
    }

    public void releaseCash(double amount) {
        if (amount <= 0) {
            return;
        }
        reservedCash = Math.max(0, reservedCash - amount);
    }

    public void settleReservedBuy(double reservedAmount, double actualCost)
            throws OrderException {
        requirePositive(reservedAmount, "Reserved amount");
        requirePositive(actualCost, "Execution cost");
        if (reservedAmount > reservedCash + EPSILON) {
            throw new OrderException("Execution exceeds reserved cash");
        }
        if (actualCost > cash + EPSILON) {
            throw new OrderException("Execution exceeds cash balance");
        }
        releaseCash(reservedAmount);
        cash -= actualCost;
        if (cash < EPSILON) {
            cash = 0;
        }
    }

    public void receiveCash(double amount) {
        if (amount <= 0 || Double.isNaN(amount) || Double.isInfinite(amount)) {
            throw new IllegalArgumentException("Cash amount must be positive and finite");
        }
        cash += amount;
    }

    public TradingStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(TradingStrategy strategy) {
        normalStrategy = strategy;
        if (activeRecessions == 0) {
            this.strategy = strategy;
        }
    }

    public void setRecessionStrategy(TradingStrategy recessionStrategy) {
        this.recessionStrategy = recessionStrategy;
        if (activeRecessions > 0 && recessionStrategy != null) {
            strategy = recessionStrategy;
        }
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
            if (recessionStrategy != null) {
                strategy = recessionStrategy;
            }
        } else if (event.getPhase() == MarketEventPhase.EXPIRED) {
            activeRecessions = Math.max(0, activeRecessions - 1);
            if (activeRecessions == 0) {
                strategy = normalStrategy;
            }
        }
        sentiment = activeRecessions > 0 ? event.getSentimentDelta() : 0;
    }

    public Order makeDecision(Stock stock, Random random, int tick) {
        if (strategy == null) {
            return null;
        }
        return strategy.decide(this, stock, random, tick);
    }

    public Order makeDecision(Stock stock, Random random) {
        return makeDecision(stock, random, 0);
    }

    private static void requirePositive(double value, String label)
            throws OrderException {
        if (value <= 0 || Double.isNaN(value) || Double.isInfinite(value)) {
            throw new OrderException(label + " must be positive and finite");
        }
    }
}