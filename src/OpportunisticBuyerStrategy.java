import java.util.Random;

public class OpportunisticBuyerStrategy implements TradingStrategy {

    @Override
    public Order decide(Trader trader, Stock stock, Random random, int tick) {
        double limitPrice = stock.getPrice() * (0.97 + random.nextDouble() * 0.025);
        double quantity = Math.min(
                15 + random.nextDouble() * 70,
                trader.getAvailableCash() / limitPrice);
        if (quantity < 0.01) {
            return null;
        }
        return new Order(
                trader,
                stock,
                OrderSide.BUY,
                OrderType.LIMIT,
                quantity,
                limitPrice,
                tick + 10);
    }
}
