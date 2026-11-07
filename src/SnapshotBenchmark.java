import java.util.List;

// Test for SnapshotBenchmark.java
// Run with:
// java -cp target/classes SnapshotBenchmark
// Expected output:
// tick 0: 0 orders, sequential 0 us, parallel 0 us, same=true
// tick 500: 1500 orders, sequential 1000 us, parallel 1000 us, same=true
// tick 2000: 6000 orders, sequential 4000 us, parallel 4000 us, same=true
// tick 8000: 24000 orders, sequential 16000 us, parallel 16000 us, same=true

/** Compares the sequential and parallel liquidity collector on a growing order book. */
public class SnapshotBenchmark {
 
    public static void main(String[] args) {
        Market market = new Market(10000, 42L, 7L);
        market.addStock(new Stock("JOON", 50));
        for (int i = 1; i <= 6; i++) {
            market.addTrader(new RetailTrader("Retail" + i, 5000));
        }
        for (int i = 1; i <= 2; i++) {
            market.addTrader(new InstitutionalTrader("Institution" + i, 100000));
        }
 
        int tick = 0;
        for (int target : List.of(500, 2000, 8000)) {
            while (tick < target) {
                market.update();
                tick++;
            }
            OrderBookAnalytics analytics = OrderBookAnalytics.from(market.getOrderBook("JOON"));
            analytics.snapshot();
            analytics.parallelSnapshot();
 
            long seqNs = time(() -> analytics.snapshot());
            long parNs = time(() -> analytics.parallelSnapshot());
            boolean same = analytics.snapshot().getBidOrderCount()
                    == analytics.parallelSnapshot().getBidOrderCount();
            System.out.printf("tick %d: %d orders, sequential %d us, parallel %d us, same=%b%n",
                    tick, market.getOrders().size(), seqNs / 1000, parNs / 1000, same);
        }
    }
 
    private static long time(Runnable task) {
        long best = Long.MAX_VALUE;
        for (int i = 0; i < 20; i++) {
            long start = System.nanoTime();
            task.run();
            best = Math.min(best, System.nanoTime() - start);
        }
        return best;
    }
}