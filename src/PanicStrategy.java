import java.util.Random;

public class PanicStrategy implements TradingStrategy {

    @Override
    public Order decide(Trader trader, Stock stock, Random random, int tick) {
        double available = trader.getPortfolio().getAvailableShares(stock);
        if (available < 0.01) {
            return null;
        }

        double quantity = Math.max(0.01, available * (0.25 + random.nextDouble() * 0.45));
        boolean marketOrder = random.nextDouble() < 0.60;
        return new Order(
                trader,
                stock,
                OrderSide.SELL,
                marketOrder ? OrderType.MARKET : OrderType.LIMIT,
                quantity,
                marketOrder ? Double.NaN : stock.getPrice() * 0.98,
                tick + 3);
    }
}
