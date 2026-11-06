import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MarketStats implements MarketEventListener {

    private final List<String> eventLog = new ArrayList<>();

    public double buyTotal(List<Trade> trades) {
        return 0;
    }

    public double sellTotal(List<Trade> trades) {
        return 0;
    }

    public double imbalance(List<Trade> trades) {
        return 0;
    }

    public Map<String, Double> volumeBySymbol(List<Trade> trades) {
        return Collections.emptyMap();
    }

    public List<String> rankTraders(List<Trade> trades) {
        return Collections.emptyList();
    }

    public Map<OrderStatus, Integer> countByStatus(List<Order> orders) {
        return Collections.emptyMap();
    }

    public double averageSlippage(List<Trade> trades) {
        return 0;
    }

    @Override
    public void onMarketEvent(MarketEvent event) {
        if (event.getType() != MarketEventType.ECONOMIC_RECESSION) {
            return;
        }
        eventLog.add(event.getType().getLabel() + " " + event.getPhase().name());
    }

    public List<String> getEventLog() {
        return eventLog;
    }
}
