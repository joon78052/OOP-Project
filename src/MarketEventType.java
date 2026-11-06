public enum MarketEventType {
    ECONOMIC_RECESSION,
    INTEREST_RATE_INCREASE,
    INTEREST_RATE_CUT,
    COMPANY_SCANDAL,
    POSITIVE_EARNINGS,
    EARNINGS_MISS,
    INSTITUTIONAL_SELL_OFF,
    GLOBAL_CRISIS,
    MARKET_RECOVERY,
    LIQUIDITY_CRISIS,
    MARKET_PANIC;

    public String getLabel() {
        if (this == ECONOMIC_RECESSION) {
            return "Economic recession";
        }
        return name();
    }
}
// What more should i add to this list?

// perhsps we can also do economic boom, i feel like these are only declines mostly so we should have something where
// theres a huge demand of a particular stock or sector, like for example rn theres demand for AI