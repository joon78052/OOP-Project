public class EventDescription {

    public static final String ECONOMIC_RECESSION = "Economic recession";
    public static final String INTEREST_RATE_INCREASE = "";
    public static final String INTEREST_RATE_CUT = "";
    public static final String COMPANY_SCANDAL = "";
    public static final String POSITIVE_EARNINGS = "";
    public static final String EARNINGS_MISS = "";
    public static final String INSTITUTIONAL_SELL_OFF = "";
    public static final String GLOBAL_CRISIS = "";
    public static final String MARKET_RECOVERY = "";
    public static final String LIQUIDITY_CRISIS = "";
    public static final String MARKET_PANIC = "";

    public static String of(MarketEventType type) {
        switch (type) {
            case ECONOMIC_RECESSION:
                return ECONOMIC_RECESSION;
            case INTEREST_RATE_INCREASE:
                return INTEREST_RATE_INCREASE;
            case INTEREST_RATE_CUT:
                return INTEREST_RATE_CUT;
            case COMPANY_SCANDAL:
                return COMPANY_SCANDAL;
            case POSITIVE_EARNINGS:
                return POSITIVE_EARNINGS;
            case EARNINGS_MISS:
                return EARNINGS_MISS;
            case INSTITUTIONAL_SELL_OFF:
                return INSTITUTIONAL_SELL_OFF;
            case GLOBAL_CRISIS:
                return GLOBAL_CRISIS;
            case MARKET_RECOVERY:
                return MARKET_RECOVERY;
            case LIQUIDITY_CRISIS:
                return LIQUIDITY_CRISIS;
            case MARKET_PANIC:
                return MARKET_PANIC;
            default:
                return "";
        }
    }

    public static String hoverText(MarketEventType type) {
        String kind = type.getLabel();
        String description = of(type);
        if (description == null || description.isEmpty()) {
            return kind;
        }
        return "<html>" + escape(kind) + "<br>" + escape(description) + "</html>";
    }

    private static String escape(String text) {
        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}
