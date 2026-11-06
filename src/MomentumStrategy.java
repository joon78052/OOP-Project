import java.util.Random;

public class MomentumStrategy implements TradingStrategy {

    @Override
    public Order decide(Trader trader, Stock stock, Random random, int tick) {
        double buyChance = 0.50 + trader.getSentiment();

        if (stock.getChangePercent() > 0) {
            buyChance = 0.60 + trader.getSentiment();
        } else if (stock.getChangePercent() < 0) {
            buyChance = 0.40 + trader.getSentiment();
        }

        buyChance = Math.max(0.05, Math.min(0.95, buyChance));

        boolean buy = random.nextDouble() < buyChance;
        if (!buy && trader.getPortfolio().getAvailableShares(stock) < 1) {
            buy = true;
        }

        double price = stock.getPrice();
        double quantity;
        double limitPrice;
        if (buy) {
            limitPrice = price * (0.995 + random.nextDouble() * 0.010);
            quantity = Math.min(
                    2 + random.nextDouble() * 18,
                    trader.getAvailableCash() / limitPrice);
        } else {
            limitPrice = price * (0.997 + random.nextDouble() * 0.010);
            quantity = Math.min(
                    2 + random.nextDouble() * 18,
                    trader.getPortfolio().getAvailableShares(stock));
        }

        if (quantity < 0.01) {
            return null;
        }
        return new Order(
                trader,
                stock,
                buy ? OrderSide.BUY : OrderSide.SELL,
                OrderType.LIMIT,
                quantity,
                limitPrice,
                tick + 8);
    }
}
