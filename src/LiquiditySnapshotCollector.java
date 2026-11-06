import java.util.stream.Collector;

public final class LiquiditySnapshotCollector {

    private static final double EPSILON = 0.0000001;

    private LiquiditySnapshotCollector() {
    }

    public static Collector<OrderSnapshot, Accumulator, LiquiditySnapshot> collector() {
        return Collector.of(
                Accumulator::new,
                Accumulator::add,
                Accumulator::combine,
                Accumulator::finish);
    }

    public static final class Accumulator {

        private double bidQuantity;
        private double askQuantity;
        private long bidCount;
        private long askCount;
        private long partialCount;
        private double bidNotional;
        private double askNotional;
        private double bestBid;
        private double bestAsk = Double.POSITIVE_INFINITY;

        private void add(OrderSnapshot order) {
            if (!order.isOpen()) {
                return;
            }
            if (order.getStatus() == OrderStatus.PARTIAL) {
                partialCount++;
            }

            double quantity = order.getRemainingQuantity();
            double notional = quantity * order.getLimitPrice();
            if (order.isBuy()) {
                bidQuantity += quantity;
                bidNotional += notional;
                bidCount++;
                bestBid = Math.max(bestBid, order.getLimitPrice());
            } else {
                askQuantity += quantity;
                askNotional += notional;
                askCount++;
                bestAsk = Math.min(bestAsk, order.getLimitPrice());
            }
        }

        private Accumulator combine(Accumulator other) {
            bidQuantity += other.bidQuantity;
            askQuantity += other.askQuantity;
            bidCount += other.bidCount;
            askCount += other.askCount;
            partialCount += other.partialCount;
            bidNotional += other.bidNotional;
            askNotional += other.askNotional;
            bestBid = Math.max(bestBid, other.bestBid);
            bestAsk = Math.min(bestAsk, other.bestAsk);
            return this;
        }

        private LiquiditySnapshot finish() {
            double weightedBid = bidQuantity <= EPSILON
                    ? 0
                    : bidNotional / bidQuantity;
            double weightedAsk = askQuantity <= EPSILON
                    ? 0
                    : askNotional / askQuantity;
            double totalQuantity = bidQuantity + askQuantity;
            double imbalance = totalQuantity <= EPSILON
                    ? 0
                    : (bidQuantity - askQuantity) / totalQuantity;

            return new LiquiditySnapshot(
                    bidQuantity,
                    askQuantity,
                    bidCount,
                    askCount,
                    partialCount,
                    bidNotional,
                    askNotional,
                    weightedBid,
                    weightedAsk,
                    imbalance,
                    bestBid,
                    bestAsk == Double.POSITIVE_INFINITY ? 0 : bestAsk);
        }
    }
}
