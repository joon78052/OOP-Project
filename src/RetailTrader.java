import java.util.Random;

public class RetailTrader extends Trader {

    public RetailTrader(String name, double cash) {
        super(name, cash);
    }

    @Override
    public Order makeDecision(Stock stock, Random random) {
        double buyChance = 0.50 + getSentiment();

        if (stock.getChangePercent() > 0) {
            buyChance = 0.60 + getSentiment();
        } else if (stock.getChangePercent() < 0) {
            buyChance = 0.40 + getSentiment();
        }

        buyChance = Math.max(0.05, Math.min(0.95, buyChance));

        boolean buy = random.nextDouble() < buyChance;
        double usd = 20 + random.nextDouble() * 280;

        return new Order(this, stock, usd, buy ? Trade.Type.BUY : Trade.Type.SELL);
    }
}