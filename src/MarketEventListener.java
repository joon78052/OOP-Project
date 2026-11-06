public interface MarketEventListener extends MarketObserver {

    @Override
    void onMarketEvent(MarketEvent event);
}
