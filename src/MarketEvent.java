public class MarketEvent {

    private final MarketEventType type;
    private final MarketEventSeverity severity;
    private final Stock stock;
    private final double sentimentDelta;
    private final int startTick;
    private final int endTick;
    private MarketEventPhase phase;

    public MarketEvent(
            MarketEventType type,
            MarketEventSeverity severity,
            Stock stock,
            double sentimentDelta,
            int startTick,
            int endTick
    ) {
        this.type = type;
        this.severity = severity;
        this.stock = stock;
        this.sentimentDelta = sentimentDelta;
        this.startTick = startTick;
        this.endTick = endTick;
        this.phase = MarketEventPhase.SCHEDULED;
    }

    public void activate() {
        if (phase == MarketEventPhase.SCHEDULED) {
            phase = MarketEventPhase.ACTIVE;
        }
    }

    public void expire() {
        if (phase != MarketEventPhase.EXPIRED) {
            phase = MarketEventPhase.EXPIRED;
        }
    }

    public double getEffect() {
        if (phase != MarketEventPhase.ACTIVE) {
            return 0;
        }
        return sentimentDelta;
    }

    public MarketEventType getType() {
        return type;
    }

    public MarketEventSeverity getSeverity() {
        return severity;
    }

    public Stock getStock() {
        return stock;
    }

    public double getSentimentDelta() {
        return sentimentDelta;
    }

    public int getStartTick() {
        return startTick;
    }

    public int getEndTick() {
        return endTick;
    }

    public MarketEventPhase getPhase() {
        return phase;
    }
}
