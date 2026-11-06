import java.util.Random;

public class InstitutionalTrader
        extends Trader {

    public InstitutionalTrader(
            String name,
            double cash
    ) {
        super(name, cash);
    }

    @Override
    public Order makeDecision(
            Stock stock,
            Random random
    ) {

        double buyChance = 0.50 + getSentiment();

        if (stock.getChangePercent() < -1.0) {
            buyChance = 0.65 + getSentiment();
        } else if (stock.getChangePercent() > 1.0) {
            buyChance = 0.35 + getSentiment();
        }

        buyChance = Math.max(0.05, Math.min(0.95, buyChance));

        boolean buy = random.nextDouble() < buyChance;

        double usd = 500 + random.nextDouble() * 2000;

        return new Order(this, stock, usd, buy ? Trade.Type.BUY : Trade.Type.SELL);
    }
}