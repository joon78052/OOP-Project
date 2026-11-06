import java.util.Random;

public class MeanReversionStrategy implements TradingStrategy {

    @Override
    public Order decide(Trader trader, Stock stock, Random random, int tick) {
        double buyChance = 0.50 + trader.getSentiment();

        if (stock.getChangePercent() < -1.0) {
            buyChance = 0.65 + trader.getSentiment();
        } else if (stock.getChangePercent() > 1.0) {
            buyChance = 0.35 + trader.getSentiment();
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
            double discount = stock.getChangePercent() < -1 ? 0.002 : 0.008;
            limitPrice = price * (1.0 - discount + random.nextDouble() * 0.006);
            quantity = Math.min(
                    20 + random.nextDouble() * 80,
                    trader.getAvailableCash() / limitPrice);
        } else {
            limitPrice = price * (0.998 + random.nextDouble() * 0.008);
            quantity = Math.min(
                    20 + random.nextDouble() * 80,
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
                tick + 12);
    }
}
