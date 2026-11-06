public interface MarketObserver {

    default void onMarketEvent(MarketEvent event) {
    }

    default void onOrderUpdate(OrderExecutionReport report) {
    }
}
