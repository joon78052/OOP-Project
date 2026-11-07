public final class Strategies {
 
    private Strategies() {
    }
 
    /** Same behaviour as OpportunisticBuyerStrategy, written as a lambda. */
    public static TradingStrategy opportunisticBuyer() {
        return limitBuyer(0.97, 0.025, 15, 70, 10);
    }
 
    /**
     * Factory for a family of buy-only strategies: no subclass needed per variation.
     * Bids between (1 - maxDiscount) and (1 - maxDiscount + spread) of the current price.
     */
    public static TradingStrategy limitBuyer(double lowFactor, double spread,
                                             double minQty, double qtyRange, int ttl) {
        return (trader, stock, random, tick) -> {
            double limitPrice = stock.getPrice() * (lowFactor + random.nextDouble() * spread);
            double quantity = Math.min(
                    minQty + random.nextDouble() * qtyRange,
                    trader.getAvailableCash() / limitPrice);
            if (quantity < 0.01) {
                return null;
            }
            return new Order(trader, stock, OrderSide.BUY, OrderType.LIMIT,
                    quantity, limitPrice, tick + ttl);
        };
    }
}
 