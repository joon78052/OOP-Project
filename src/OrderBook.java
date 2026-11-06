import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class OrderBook {

    private final Stock stock;
    private final Comparator<Order> buyPriority =
            Comparator.comparingDouble(Order::getLimitPrice)
                    .reversed()
                    .thenComparingLong(Order::getSequence);
    private final Comparator<Order> sellPriority =
            Comparator.comparingDouble(Order::getLimitPrice)
                    .thenComparingLong(Order::getSequence);
    private final PriorityQueue<Order> bids = new PriorityQueue<>(buyPriority);
    private final PriorityQueue<Order> asks = new PriorityQueue<>(sellPriority);
    private final List<Order> history = new ArrayList<>();

    public OrderBook(Stock stock) {
        this.stock = stock;
    }

    public Stock getStock() {
        return stock;
    }

    void recordSubmission(Order order) {
        history.add(order);
    }

    void addResting(Order order) {
        if (order.getOrderType() != OrderType.LIMIT || !order.isOpen()) {
            throw new IllegalArgumentException("Only open limit orders can rest");
        }
        queueFor(order.getSide()).add(order);
    }

    void remove(Order order) {
        queueFor(order.getSide()).remove(order);
    }

    Order bestCompatible(Order incoming) {
        Comparator<Order> priority =
                incoming.isBuy() ? sellPriority : buyPriority;
        return queueFor(opposite(incoming.getSide())).stream()
                .filter(Order::isOpen)
                .filter(order -> order.getTrader() != incoming.getTrader())
                .filter(order -> pricesCross(incoming, order))
                .sorted(priority)
                .findFirst()
                .orElse(null);
    }

    List<Order> compatibleOppositeOrders(Order incoming) {
        Comparator<Order> priority =
                incoming.isBuy() ? sellPriority : buyPriority;
        return queueFor(opposite(incoming.getSide())).stream()
                .filter(Order::isOpen)
                .filter(order -> order.getTrader() != incoming.getTrader())
                .filter(order -> pricesCross(incoming, order))
                .sorted(priority)
                .toList();
    }

    List<Order> openOrders() {
        return history.stream()
                .filter(Order::isOpen)
                .toList();
    }

    Order findOrder(long orderId) {
        return history.stream()
                .filter(order -> order.getOrderId() == orderId)
                .findFirst()
                .orElse(null);
    }

    public List<OrderSnapshot> snapshot() {
        return history.stream()
                .map(OrderSnapshot::new)
                .toList();
    }

    public List<OrderSnapshot> getPendingBuyOrders(String symbol) {
        return snapshot().stream()
                .filter(order -> order.getStockSymbol().equals(symbol))
                .filter(OrderSnapshot::isBuy)
                .filter(OrderSnapshot::isOpen)
                .sorted(Comparator.comparingDouble(OrderSnapshot::getLimitPrice)
                        .reversed()
                        .thenComparingLong(OrderSnapshot::getSequence))
                .toList();
    }

    public List<OrderSnapshot> getPendingSellOrders(String symbol) {
        return snapshot().stream()
                .filter(order -> order.getStockSymbol().equals(symbol))
                .filter(order -> !order.isBuy())
                .filter(OrderSnapshot::isOpen)
                .sorted(Comparator.comparingDouble(OrderSnapshot::getLimitPrice)
                        .thenComparingLong(OrderSnapshot::getSequence))
                .toList();
    }

    public List<OrderSnapshot> getPartiallyFilledOrders(String symbol) {
        return snapshot().stream()
                .filter(order -> order.getStockSymbol().equals(symbol))
                .filter(order -> order.getStatus() == OrderStatus.PARTIAL)
                .toList();
    }

    public List<OrderSnapshot> getOrdersByTrader(String traderId) {
        return snapshot().stream()
                .filter(order -> order.getTraderId().equals(traderId))
                .sorted(Comparator.comparingLong(OrderSnapshot::getSequence))
                .toList();
    }

    public double getBestBid() {
        discardClosed(bids);
        return bids.isEmpty() ? 0 : bids.peek().getLimitPrice();
    }

    public double getBestAsk() {
        discardClosed(asks);
        return asks.isEmpty() ? 0 : asks.peek().getLimitPrice();
    }

    private PriorityQueue<Order> queueFor(OrderSide side) {
        return side == OrderSide.BUY ? bids : asks;
    }

    private OrderSide opposite(OrderSide side) {
        return side == OrderSide.BUY ? OrderSide.SELL : OrderSide.BUY;
    }

    private boolean pricesCross(Order incoming, Order resting) {
        if (incoming.getOrderType() == OrderType.MARKET) {
            return true;
        }
        if (incoming.isBuy()) {
            return incoming.getLimitPrice() >= resting.getLimitPrice();
        }
        return incoming.getLimitPrice() <= resting.getLimitPrice();
    }

    private void discardClosed(PriorityQueue<Order> queue) {
        while (!queue.isEmpty() && !queue.peek().isOpen()) {
            queue.poll();
        }
    }
}
