import java.util.Random;

public class MeanReversionStrategy implements TradingStrategy {

    @Override
    public Order decide(Trader trader, Stock stock, Random random) {
        double buyChance = 0.50 + trader.getSentiment();

        if (stock.getChangePercent() < -1.0) {
            buyChance = 0.65 + trader.getSentiment();
        } else if (stock.getChangePercent() > 1.0) {
            buyChance = 0.35 + trader.getSentiment();
        }

        buyChance = Math.max(0.05, Math.min(0.95, buyChance));

        boolean buy = random.nextDouble() < buyChance;
        double usd = 500 + random.nextDouble() * 2000;

        return new Order(trader, stock, usd, buy ? Trade.Type.BUY : Trade.Type.SELL);
    }
}
