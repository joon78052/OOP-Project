import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MarketStats implements MarketEventListener {

    private final List<String> eventLog = new ArrayList<>();
    private OrderExecutionReport latestOrderReport;

    public double buyTotal(List<Trade> trades) {
        return trades.stream()
                .mapToDouble(Trade::getTotalUsd)
                .sum();
    }

    public double sellTotal(List<Trade> trades) {
        return trades.stream()
                .mapToDouble(Trade::getTotalUsd)
                .sum();
    }

    public double imbalance(List<Trade> trades) {
        // Every execution has one buyer and one seller. Book imbalance is
        // therefore calculated from open orders in OrderBookAnalytics.
        return 0;
    }

    public Map<String, Double> volumeBySymbol(List<Trade> trades) {
        return executedVolumeByStock(trades);
    }

    public List<String> rankTraders(List<Trade> trades) {
        Map<String, Double> netCashFlow = trades.stream()
                .flatMap(trade -> Stream.of(
                        new AbstractMap.SimpleEntry<>(
                                trade.getBuyerName(),
                                -trade.getTotalUsd()),
                        new AbstractMap.SimpleEntry<>(
                                trade.getSellerName(),
                                trade.getTotalUsd())))
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.summingDouble(Map.Entry::getValue)));

        return netCashFlow.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .toList();
    }

    public Map<OrderStatus, Long> countByStatus(List<OrderSnapshot> orders) {
        return ordersByStatus(orders);
    }

    public double averageSlippage(List<Trade> trades) {
        // A limit or reference price is required to calculate slippage.
        return 0;
    }

    public double averageSlippage(
            List<Trade> trades,
            List<OrderSnapshot> orders
    ) {
        Map<Long, OrderSnapshot> byId = orders.stream()
                .collect(Collectors.toMap(
                        OrderSnapshot::getOrderId,
                        order -> order,
                        (first, second) -> first,
                        HashMap::new));

        return trades.stream()
                .flatMapToDouble(trade -> Stream.of(
                                slippage(byId.get(trade.getBuyOrderId()), trade.getPrice()),
                                slippage(byId.get(trade.getSellOrderId()), trade.getPrice()))
                        .filter(value -> !Double.isNaN(value))
                        .mapToDouble(Double::doubleValue))
                .average()
                .orElse(0);
    }

    public Map<String, Long> pendingOrdersByStock(List<OrderSnapshot> orders) {
        return orders.stream()
                .filter(OrderSnapshot::isOpen)
                .collect(Collectors.groupingBy(
                        OrderSnapshot::getStockSymbol,
                        Collectors.counting()));
    }

    public Map<OrderStatus, Long> ordersByStatus(List<OrderSnapshot> orders) {
        return orders.stream()
                .collect(Collectors.groupingBy(
                        OrderSnapshot::getStatus,
                        Collectors.counting()));
    }

    public Map<String, Double> executedVolumeByStock(List<Trade> trades) {
        return trades.stream()
                .collect(Collectors.groupingBy(
                        trade -> trade.getStock().getSymbol(),
                        Collectors.summingDouble(Trade::getQuantity)));
    }

    public Map<String, Map<OrderStatus, Long>> ordersByStockAndStatus(
            List<OrderSnapshot> orders
    ) {
        return orders.stream()
                .collect(Collectors.groupingBy(
                        OrderSnapshot::getStockSymbol,
                        Collectors.groupingBy(
                                OrderSnapshot::getStatus,
                                Collectors.counting())));
    }

    @Override
    public void onMarketEvent(MarketEvent event) {
        if (event.getType() != MarketEventType.ECONOMIC_RECESSION) {
            return;
        }
        eventLog.add(event.getType().getLabel() + " " + event.getPhase().name());
    }

    @Override
    public void onOrderUpdate(OrderExecutionReport report) {
        latestOrderReport = report;
    }

    public List<String> getEventLog() {
        return List.copyOf(eventLog);
    }

    public OrderExecutionReport getLatestOrderReport() {
        return latestOrderReport;
    }

    private double slippage(OrderSnapshot order, double executionPrice) {
        if (order == null || order.getOrderType() != OrderType.LIMIT) {
            return Double.NaN;
        }
        if (order.isBuy()) {
            return (order.getLimitPrice() - executionPrice) / order.getLimitPrice();
        }
        return (executionPrice - order.getLimitPrice()) / order.getLimitPrice();
    }
}
