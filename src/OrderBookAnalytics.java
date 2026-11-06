import java.util.List;

public class OrderBookAnalytics {

    private static final double EPSILON = 0.0000001;

    private final List<OrderSnapshot> orders;

    public OrderBookAnalytics(List<OrderSnapshot> orders) {
        this.orders = List.copyOf(orders);
    }

    public static OrderBookAnalytics from(OrderBook book) {
        return new OrderBookAnalytics(book == null ? List.of() : book.snapshot());
    }

    public LiquiditySnapshot snapshot() {
        return orders.stream().collect(LiquiditySnapshotCollector.collector());
    }

    public LiquiditySnapshot parallelSnapshot() {
        return orders.parallelStream().collect(LiquiditySnapshotCollector.collector());
    }

    public double getTotalAvailableBidQuantity() {
        return snapshot().getTotalBidQuantity();
    }

    public double getTotalAvailableAskQuantity() {
        return snapshot().getTotalAskQuantity();
    }

    public double getTotalBidNotional() {
        return snapshot().getTotalBidNotional();
    }

    public double getTotalAskNotional() {
        return snapshot().getTotalAskNotional();
    }

    public double getBuySellImbalance() {
        return snapshot().getBuySellImbalance();
    }

    public long getUnfilledOrderCount() {
        return orders.stream()
                .filter(OrderSnapshot::isOpen)
                .filter(order -> order.getFilledQuantity() <= EPSILON)
                .count();
    }

    public long getPartiallyFilledOrderCount() {
        return orders.stream()
                .filter(order -> order.getStatus() == OrderStatus.PARTIAL)
                .count();
    }

    public double getAverageBidPrice() {
        return snapshot().getWeightedAverageBidPrice();
    }

    public double getAverageAskPrice() {
        return snapshot().getWeightedAverageAskPrice();
    }

    public double getBidAskSpread() {
        return snapshot().getSpread();
    }

    public double getSellQuantityExecutionPercentage() {
        double submitted = orders.stream()
                .filter(order -> order.getSide() == OrderSide.SELL)
                .mapToDouble(OrderSnapshot::getOriginalQuantity)
                .sum();
        if (submitted <= EPSILON) {
            return 0;
        }
        double filled = orders.stream()
                .filter(order -> order.getSide() == OrderSide.SELL)
                .mapToDouble(OrderSnapshot::getFilledQuantity)
                .sum();
        return (filled / submitted) * 100.0;
    }
}
