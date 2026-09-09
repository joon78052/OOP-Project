import java.util.ArrayList;
import java.util.Random;

public class Market {

    private static final int MAX_TRADES_KEPT = 100;

    private ArrayList<Stock> stocks;
    private ArrayList<Trader> traders;
    private ArrayList<Trade> trades;
    private Player player;

    private Random random;
    private int tick;

    public Market(double startingCash) {
        stocks = new ArrayList<>();
        traders = new ArrayList<>();
        trades = new ArrayList<>();
        player = new Player("You", startingCash);

        random = new Random();
        tick = 0;
    }

    public void update() {

        tick++;

        for (Stock stock : stocks) {

            int tradeCount = 1 + random.nextInt(4);

            double price = stock.getPrice();

            double tickHigh = price;
            double tickLow = price;

            double volumeUsd = 0;

            for (int i = 0; i < tradeCount; i++) {

                boolean isBuy = random.nextBoolean();

                double usd = 20 + random.nextDouble() * 1200;
                double quantity = usd / price;

                double impact = (usd / 150_000.0) * (isBuy ? 1 : -1);
                price = Math.max(0.01, price * (1 + impact));

                tickHigh = Math.max(tickHigh, price);
                tickLow = Math.min(tickLow, price);

                Trade trade = new Trade(
                        isBuy ? Trade.Type.BUY : Trade.Type.SELL,
                        stock,
                        price,
                        quantity,
                        randomTraderTag(),
                        tick
                );

                recordTrade(trade);
                volumeUsd += usd;
            }

            stock.updatePrice(
                price,
                tickHigh,
                tickLow,
                volumeUsd
            );
        }
    }

    public void playerBuy(Stock stock, double usd) throws OrderException {
        checkAmount(usd);
        double price = stock.getPrice();
        double quantity = usd / price;
        player.spend(usd);
        player.getPortfolio().addShares(stock, quantity);
        applyPlayerTrade(stock, true, usd, quantity);
    }

    public void playerSellUsd(Stock stock, double usd) throws OrderException {
        checkAmount(usd);
        double price = stock.getPrice();
        double quantity = usd / price;
        playerSellShares(stock, quantity);
    }

    public void playerSellPercent(Stock stock, double percent) throws OrderException {
        if (percent <= 0 || percent > 1) {
            throw new OrderException("Invalid sell percent");
        }
        double quantity = player.getPortfolio().getShares(stock) * percent;
        if (quantity <= 0) {
            throw new OrderException("No shares to sell");
        }
        playerSellShares(stock, quantity);
    }

    private void playerSellShares(Stock stock, double quantity) throws OrderException {
        player.getPortfolio().removeShares(stock, quantity);
        double usd = quantity * stock.getPrice();
        player.receive(usd);
        applyPlayerTrade(stock, false, usd, quantity);
    }

    private void applyPlayerTrade(Stock stock, boolean isBuy, double usd, double quantity) {
        double impact = (usd / 50_000.0) * (isBuy ? 1 : -1);
        double newPrice = Math.max(0.01, stock.getPrice() * (1 + impact));

        Trade trade = new Trade(
                isBuy ? Trade.Type.BUY : Trade.Type.SELL,
                stock,
                newPrice,
                quantity,
                player.getName(),
                tick
        );

        recordTrade(trade);
        stock.updatePrice(newPrice, usd);
    }

    private void checkAmount(double usd) throws OrderException {
        if (usd <= 0 || Double.isNaN(usd) || Double.isInfinite(usd)) {
            throw new OrderException("Enter an amount greater than 0");
        }
    }

    private void recordTrade(Trade trade) {
        trades.add(trade);
        if (trades.size() > MAX_TRADES_KEPT) {
            trades.remove(0);
        }
    }

    private String randomTraderTag() {
        return "Trader" + (1 + random.nextInt(99));
    }

    public void addStock(Stock stock) {
        stocks.add(stock);
    }

    public void addTrader(Trader trader) {
        traders.add(trader);
    }

    public Player getPlayer() {
        return player;
    }

    public ArrayList<Stock> getStocks() {
        return stocks;
    }

    public ArrayList<Trade> getTrades() {
        return trades;
    }

    public int getTick() {
        return tick;
    }
}
