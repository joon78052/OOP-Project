public class SimulationCheck {
 
    public static void main(String[] args) {
        check("same seeds give the same price", finalPrice(42L, 7L) == finalPrice(42L, 7L));
        check("lambda strategy places buy orders", lambdaStrategyBuys());
        System.out.println("All checks passed");
    }
 
    private static double finalPrice(long marketSeed, long eventSeed) {
        Market market = new Market(10000, marketSeed, eventSeed);
        market.addStock(new Stock("JOON", 50));
        for (int i = 1; i <= 6; i++) {
            market.addTrader(new RetailTrader("Retail" + i, 5000));
        }
        for (int i = 0; i < 1000; i++) {
            market.update();
        }
        return market.getStocks().get(0).getPrice();
    }
 
    private static boolean lambdaStrategyBuys() {
        Trader trader = new RetailTrader("T", 100000);
        Stock stock = new Stock("X", 10);
        Order order = Strategies.opportunisticBuyer().decide(trader, stock, new java.util.Random(1), 0);
        return order != null && order.isBuy();
    }
 
    private static void check(String name, boolean ok) {
        if (!ok) {
            throw new AssertionError("FAILED: " + name);
        }
        System.out.println("ok: " + name);
    }
}