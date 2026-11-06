public final class LiquiditySnapshot {

    private final double totalBidQuantity;
    private final double totalAskQuantity;
    private final long bidOrderCount;
    private final long askOrderCount;
    private final long partiallyFilledOrderCount;
    private final double totalBidNotional;
    private final double totalAskNotional;
    private final double weightedAverageBidPrice;
    private final double weightedAverageAskPrice;
    private final double buySellImbalance;
    private final double bestBid;
    private final double bestAsk;

    public LiquiditySnapshot(
            double totalBidQuantity,
            double totalAskQuantity,
            long bidOrderCount,
            long askOrderCount,
            long partiallyFilledOrderCount,
            double totalBidNotional,
            double totalAskNotional,
            double weightedAverageBidPrice,
            double weightedAverageAskPrice,
            double buySellImbalance,
            double bestBid,
            double bestAsk
    ) {
        this.totalBidQuantity = totalBidQuantity;
        this.totalAskQuantity = totalAskQuantity;
        this.bidOrderCount = bidOrderCount;
        this.askOrderCount = askOrderCount;
        this.partiallyFilledOrderCount = partiallyFilledOrderCount;
        this.totalBidNotional = totalBidNotional;
        this.totalAskNotional = totalAskNotional;
        this.weightedAverageBidPrice = weightedAverageBidPrice;
        this.weightedAverageAskPrice = weightedAverageAskPrice;
        this.buySellImbalance = buySellImbalance;
        this.bestBid = bestBid;
        this.bestAsk = bestAsk;
    }

    public double getTotalBidQuantity() { return totalBidQuantity; }
    public double getTotalAskQuantity() { return totalAskQuantity; }
    public long getBidOrderCount() { return bidOrderCount; }
    public long getAskOrderCount() { return askOrderCount; }
    public long getPartiallyFilledOrderCount() { return partiallyFilledOrderCount; }
    public double getTotalBidNotional() { return totalBidNotional; }
    public double getTotalAskNotional() { return totalAskNotional; }
    public double getWeightedAverageBidPrice() { return weightedAverageBidPrice; }
    public double getWeightedAverageAskPrice() { return weightedAverageAskPrice; }
    public double getBuySellImbalance() { return buySellImbalance; }
    public double getBestBid() { return bestBid; }
    public double getBestAsk() { return bestAsk; }

    public double getSpread() {
        if (bestBid <= 0 || bestAsk <= 0) {
            return 0;
        }
        return bestAsk - bestBid;
    }

    public long getOpenOrderCount() {
        return bidOrderCount + askOrderCount;
    }
}
