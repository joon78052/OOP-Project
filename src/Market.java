import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Market {

    private final ArrayList<Stock> stocks;
    private final ArrayList<Trader> traders;
    private final Player player;
    private final Random random;
    private final MarketEventBus eventBus;
    private final MarketEventGenerator eventGenerator;
    private final MarketStats stats;
    private final MatchingEngine matchingEngine;
    private int tick;
    private OrderExecutionReport latestPlayerReport;

    public Market(double startingCash) {
        this(startingCash, System.nanoTime(), System.nanoTime());
    }

    public Market(double startingCash, long marketSeed, long eventSeed) {
        stocks = new ArrayList<>();
        traders = new ArrayList<>();
        player = new Player("You", startingCash);
        random = new Random(marketSeed);
        tick = 0;
        eventBus = new MarketEventBus();
        eventGenerator = new MarketEventGenerator(eventSeed, eventBus);
        stats = new MarketStats();
        eventBus.add(stats);
        matchingEngine = new MatchingEngine(eventBus);
    }

    public void update() {
        tick++;
        eventGenerator.tick(tick);
        matchingEngine.expireOrders(tick);

        for (Stock stock : stocks) {
            for (Trader trader : traders) {
                Order order = trader.makeDecision(stock, random, tick);
                if (order != null) {
                    matchingEngine.submit(order, tick);
                }
            }
        }

        for (Stock stock : stocks) {
            stock.closeTick();
        }
    }

    public void playerBuy(Stock stock, double usd) throws OrderException {
        checkAmount(usd);
        submitPlayerOrder(
                stock,
                OrderSide.BUY,
                OrderType.MARKET,
                usd / stock.getPrice(),
                Double.NaN,
                -1);
    }

    public void playerSellUsd(Stock stock, double usd) throws OrderException {
        checkAmount(usd);
        submitPlayerOrder(
                stock,
                OrderSide.SELL,
                OrderType.MARKET,
                usd / stock.getPrice(),
                Double.NaN,
                -1);
    }

    public void playerSellPercent(Stock stock, double percent) throws OrderException {
        if (percent <= 0 || percent > 1) {
            throw new OrderException("Invalid sell percent");
        }
        double quantity = player.getPortfolio().getShares(stock) * percent;
        if (quantity <= 0) {
            throw new OrderException("No shares to sell");
        }
        submitPlayerOrder(
                stock,
                OrderSide.SELL,
                OrderType.MARKET,
                quantity,
                Double.NaN,
                -1);
    }

    public OrderExecutionReport submitPlayerOrder(
            Stock stock,
            OrderSide side,
            OrderType type,
            double quantity,
            double limitPrice,
            int expiryTicks
    ) throws OrderException {
        if (!stocks.contains(stock)) {
            throw new OrderException("Unknown stock");
        }
        checkAmount(quantity);
        if (type == OrderType.LIMIT) {
            checkAmount(limitPrice);
        }
        int expiryTick = type == OrderType.LIMIT && expiryTicks > 0
                ? tick + expiryTicks
                : -1;
        Order order = new Order(
                player,
                stock,
                side,
                type,
                quantity,
                limitPrice,
                expiryTick);
        latestPlayerReport = matchingEngine.submit(order, tick);
        return latestPlayerReport;
    }

    public OrderExecutionReport cancelPlayerOrder(long orderId, String symbol) {
        latestPlayerReport = matchingEngine.cancel(orderId, symbol);
        return latestPlayerReport;
    }

    private void checkAmount(double value) throws OrderException {
        if (value <= 0 || Double.isNaN(value) || Double.isInfinite(value)) {
            throw new OrderException("Enter an amount greater than 0");
        }
    }

    public void addStock(Stock stock) {
        stocks.add(stock);
        matchingEngine.registerStock(stock);
    }

    public void addTrader(Trader trader) {
        traders.add(trader);
        eventBus.add(trader);
        for (Stock stock : stocks) {
            double initialShares = Math.max(
                    100,
                    Math.min(1000, trader.getCash() / 100));
            trader.getPortfolio().addShares(stock, initialShares);
        }
    }

    public Player getPlayer() {
        return player;
    }

    public ArrayList<Stock> getStocks() {
        return stocks;
    }

    public ArrayList<Trade> getTrades() {
        return new ArrayList<>(matchingEngine.getTradeSnapshot());
    }

    public List<OrderSnapshot> getOrders() {
        return matchingEngine.getOrderSnapshot();
    }

    public OrderBook getOrderBook(String symbol) {
        return matchingEngine.getOrderBook(symbol);
    }

    public MatchingEngine getMatchingEngine() {
        return matchingEngine;
    }

    public OrderExecutionReport getLatestPlayerReport() {
        return latestPlayerReport;
    }

    public int getTick() {
        return tick;
    }

    public MarketEventBus getEventBus() {
        return eventBus;
    }

    public MarketEventGenerator getEventGenerator() {
        return eventGenerator;
    }

    public MarketStats getStats() {
        return stats;
    }
}
