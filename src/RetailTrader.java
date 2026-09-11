import java.util.Random;

public class RetailTrader extends Trader {

    public RetailTrader(String name, double cash) {
        super(name, cash);
    }

    @Override
    public Order makeDecision(Stock stock, Random random) {
        double buyChance = 0.50;

        if (stock.getChangePercent() > 0) {
            buyChance = 0.60;
        } else if (stock.getChangePercent() < 0) {
            buyChance = 0.40;
        }

        boolean buy = random.nextDouble() < buyChance;
        double usd = 20 + random.nextDouble() * 280;

        return new Order(this, stock, usd, buy ? Trade.Type.BUY : Trade.Type.SELL);
    }
}