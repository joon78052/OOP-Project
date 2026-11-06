public final class OrderSnapshot {

    private final long orderId;
    private final long sequence;
    private final String traderId;
    private final String stockSymbol;
    private final OrderSide side;
    private final OrderType orderType;
    private final double originalQuantity;
    private final double remainingQuantity;
    private final double filledQuantity;
    private final double limitPrice;
    private final double averageExecutionPrice;
    private final int submissionTick;
    private final int expiryTick;
    private final OrderStatus status;
    private final String statusReason;

    public OrderSnapshot(Order order) {
        orderId = order.getOrderId();
        sequence = order.getSequence();
        traderId = order.getTraderId();
        stockSymbol = order.getStockSymbol();
        side = order.getSide();
        orderType = order.getOrderType();
        originalQuantity = order.getOriginalQuantity();
        remainingQuantity = order.getRemainingQuantity();
        filledQuantity = order.getFilledQuantity();
        limitPrice = order.getLimitPrice();
        averageExecutionPrice = order.getAverageExecutionPrice();
        submissionTick = order.getSubmissionTick();
        expiryTick = order.getExpiryTick();
        status = order.getStatus();
        statusReason = order.getStatusReason();
    }

    public long getOrderId() { return orderId; }
    public long getSequence() { return sequence; }
    public String getTraderId() { return traderId; }
    public String getStockSymbol() { return stockSymbol; }
    public OrderSide getSide() { return side; }
    public OrderType getOrderType() { return orderType; }
    public double getOriginalQuantity() { return originalQuantity; }
    public double getRemainingQuantity() { return remainingQuantity; }
    public double getFilledQuantity() { return filledQuantity; }
    public double getLimitPrice() { return limitPrice; }
    public double getAverageExecutionPrice() { return averageExecutionPrice; }
    public int getSubmissionTick() { return submissionTick; }
    public int getExpiryTick() { return expiryTick; }
    public OrderStatus getStatus() { return status; }
    public String getStatusReason() { return statusReason; }
    public boolean isBuy() { return side == OrderSide.BUY; }
    public boolean isOpen() {
        return status == OrderStatus.PENDING || status == OrderStatus.PARTIAL;
    }
}
