import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MatchingEngine {

    private static final double EPSILON = 0.0000001;

    private final Map<String, OrderBook> books = new LinkedHashMap<>();
    private final List<Trade> trades = new ArrayList<>();
    private final MarketEventBus eventBus;
    private long nextOrderId = 1;
    private long nextSequence = 1;
    private long nextExecutionId = 1;

    public MatchingEngine(MarketEventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void registerStock(Stock stock) {
        if (books.containsKey(stock.getSymbol())) {
            throw new IllegalArgumentException("Duplicate stock symbol " + stock.getSymbol());
        }
        books.put(stock.getSymbol(), new OrderBook(stock));
    }

    public OrderExecutionReport submit(Order order, int tick) {
        OrderBook book = books.get(order.getStockSymbol());
        order.accept(nextOrderId++, nextSequence++, tick);
        List<Trade> executions = new ArrayList<>();

        if (book == null) {
            order.reject("Stock is not registered with this market");
            return publish(order, executions);
        }

        book.recordSubmission(order);
        if (order.isExpiredAt(tick)) {
            order.expire();
            return publish(order, executions);
        }

        try {
            reserve(order, book);
            match(order, book, tick, executions);

            if (order.isOpen() && order.getOrderType() == OrderType.MARKET) {
                order.cancel(order.getFilledQuantity() > EPSILON
                        ? "Unfilled market quantity cancelled: insufficient liquidity"
                        : "No compatible liquidity was available");
                releaseReservation(order);
            } else if (order.isOpen()) {
                book.addResting(order);
            } else {
                releaseReservation(order);
            }
        } catch (OrderException exception) {
            releaseReservation(order);
            order.reject(exception.getMessage());
        }

        return publish(order, executions);
    }

    public OrderExecutionReport cancel(long orderId, String symbol) {
        OrderBook book = books.get(symbol);
        if (book == null) {
            return null;
        }
        Order order = book.findOrder(orderId);
        if (order == null || !order.isOpen()) {
            return null;
        }
        book.remove(order);
        releaseReservation(order);
        order.cancel("Cancelled by trader");
        return publish(order, List.of());
    }

    public void expireOrders(int tick) {
        for (OrderBook book : books.values()) {
            for (Order order : book.openOrders()) {
                if (!order.isExpiredAt(tick)) {
                    continue;
                }
                book.remove(order);
                releaseReservation(order);
                order.expire();
                publish(order, List.of());
            }
        }
    }

    public OrderBook getOrderBook(String symbol) {
        return books.get(symbol);
    }

    public List<OrderSnapshot> getOrderSnapshot() {
        return books.values().stream()
                .flatMap(book -> book.snapshot().stream())
                .toList();
    }

    public List<Trade> getTradeSnapshot() {
        return List.copyOf(trades);
    }

    private void reserve(Order order, OrderBook book) throws OrderException {
        if (!order.isBuy()) {
            order.getTrader().getPortfolio()
                    .reserveShares(order.getStock(), order.getOriginalQuantity());
            order.setReservedShares(order.getOriginalQuantity());
            return;
        }

        double amount;
        if (order.getOrderType() == OrderType.LIMIT) {
            amount = order.getOriginalQuantity() * order.getLimitPrice();
        } else {
            amount = estimateMarketBuyReservation(order, book);
        }

        if (amount > EPSILON) {
            order.getTrader().reserveCash(amount);
            order.setReservedCash(amount);
        }
    }

    private double estimateMarketBuyReservation(Order order, OrderBook book) {
        double quantityLeft = order.getOriginalQuantity();
        double cashLeft = order.getTrader().getAvailableCash();
        double reservation = 0;

        for (Order ask : book.compatibleOppositeOrders(order)) {
            if (quantityLeft <= EPSILON || cashLeft <= EPSILON) {
                break;
            }
            double price = ask.getLimitPrice();
            double quantity = Math.min(quantityLeft, ask.getRemainingQuantity());
            quantity = Math.min(quantity, cashLeft / price);
            if (quantity <= EPSILON) {
                break;
            }
            double cost = quantity * price;
            reservation += cost;
            cashLeft -= cost;
            quantityLeft -= quantity;
        }
        return reservation;
    }

    private void match(
            Order incoming,
            OrderBook book,
            int tick,
            List<Trade> incomingExecutions
    ) throws OrderException {
        while (incoming.isOpen() && incoming.getRemainingQuantity() > EPSILON) {
            Order resting = book.bestCompatible(incoming);
            if (resting == null) {
                return;
            }

            double executionPrice = resting.getLimitPrice();
            Order buyOrder = incoming.isBuy() ? incoming : resting;
            Order sellOrder = incoming.isBuy() ? resting : incoming;
            double quantity = Math.min(
                    incoming.getRemainingQuantity(),
                    resting.getRemainingQuantity());
            quantity = Math.min(
                    quantity,
                    affordableQuantity(buyOrder, executionPrice));

            if (quantity <= EPSILON) {
                return;
            }

            Trade trade = settle(
                    buyOrder,
                    sellOrder,
                    executionPrice,
                    quantity,
                    tick);
            incomingExecutions.add(trade);

            if (!resting.isOpen()) {
                book.remove(resting);
                releaseReservation(resting);
            }
            eventBus.publishOrderUpdate(
                    new OrderExecutionReport(resting, List.of(trade)));
        }
    }

    private double affordableQuantity(Order buyOrder, double executionPrice) {
        return buyOrder.getReservedCash() / executionPrice;
    }

    private Trade settle(
            Order buyOrder,
            Order sellOrder,
            double price,
            double quantity,
            int tick
    ) throws OrderException {
        double cost = price * quantity;
        double buyReservation = buyOrder.getOrderType() == OrderType.LIMIT
                ? buyOrder.getLimitPrice() * quantity
                : cost;

        if (buyReservation > buyOrder.getReservedCash() + EPSILON) {
            throw new OrderException("Buyer reservation is insufficient");
        }
        if (quantity > sellOrder.getReservedShares() + EPSILON) {
            throw new OrderException("Seller reservation is insufficient");
        }

        buyOrder.getTrader().settleReservedBuy(buyReservation, cost);
        buyOrder.reduceReservedCash(buyReservation);
        sellOrder.getTrader().getPortfolio()
                .settleReservedSale(sellOrder.getStock(), quantity);
        sellOrder.reduceReservedShares(quantity);
        buyOrder.getTrader().getPortfolio().addShares(buyOrder.getStock(), quantity);
        sellOrder.getTrader().receiveCash(cost);

        buyOrder.recordFill(quantity, price);
        sellOrder.recordFill(quantity, price);

        Trade trade = new Trade(
                nextExecutionId++,
                buyOrder.getStock(),
                price,
                quantity,
                buyOrder.getTrader(),
                sellOrder.getTrader(),
                buyOrder.getOrderId(),
                sellOrder.getOrderId(),
                tick);
        trades.add(trade);
        buyOrder.getStock().recordExecution(price, quantity);
        return trade;
    }

    private void releaseReservation(Order order) {
        if (order.getReservedCash() > EPSILON) {
            order.getTrader().releaseCash(order.getReservedCash());
            order.setReservedCash(0);
        }
        if (order.getReservedShares() > EPSILON) {
            order.getTrader().getPortfolio()
                    .releaseShares(order.getStock(), order.getReservedShares());
            order.setReservedShares(0);
        }
    }

    private OrderExecutionReport publish(Order order, List<Trade> executions) {
        OrderExecutionReport report = new OrderExecutionReport(order, executions);
        eventBus.publishOrderUpdate(report);
        return report;
    }
}
