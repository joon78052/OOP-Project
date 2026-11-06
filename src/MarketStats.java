import java.util.Collections;
import java.util.List;
import java.util.Map;


public class MarketStats {

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
}
