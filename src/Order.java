public class Order {

    private final Trader trader;
    private final Stock stock;
    private final double usd;
    private final Trade.Type type;

    public Order(
            Trader trader,
            Stock stock,
            double usd,
            Trade.Type type
    ) {
        this.trader = trader;
        this.stock = stock;
        this.usd = usd;
        this.type = type;
    }

    public Trader getTrader() {
        return trader;
    }

    public Stock getStock() {
        return stock;
    }

    public double getUsd() {
        return usd;
    }

    public Trade.Type getType() {
        return type;
    }

    public boolean isBuy() {
        return type == Trade.Type.BUY;
    }
}