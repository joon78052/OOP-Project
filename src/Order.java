public class Order {

    private static final double EPSILON = 0.0000001;

    private long orderId;
    private long sequence;
    private int submissionTick;
    private final int expiryTick;
    private final Trader trader;
    private final Stock stock;
    private final OrderSide side;
    private final OrderType orderType;
    private final double originalQuantity;
    private final double limitPrice;
    private double remainingQuantity;
    private double filledQuantity;
    private double executedNotional;
    private double reservedCash;
    private double reservedShares;
    private OrderStatus status;
    private String statusReason;

    public Order(
            Trader trader,
            Stock stock,
            OrderSide side,
            OrderType orderType,
            double quantity,
            double limitPrice,
            int expiryTick
    ) {
        if (trader == null || stock == null || side == null || orderType == null) {
            throw new IllegalArgumentException("Order fields cannot be null");
        }
        if (quantity <= 0 || Double.isNaN(quantity) || Double.isInfinite(quantity)) {
            throw new IllegalArgumentException("Quantity must be positive and finite");
        }
        if (orderType == OrderType.LIMIT
                && (limitPrice <= 0
                || Double.isNaN(limitPrice)
                || Double.isInfinite(limitPrice))) {
            throw new IllegalArgumentException("Limit price must be positive and finite");
        }

        this.trader = trader;
        this.stock = stock;
        this.side = side;
        this.orderType = orderType;
        this.originalQuantity = quantity;
        this.remainingQuantity = quantity;
        this.limitPrice = orderType == OrderType.LIMIT ? limitPrice : Double.NaN;
        this.expiryTick = expiryTick;
        this.status = OrderStatus.PENDING;
        this.statusReason = "Waiting for a compatible counterparty";
    }

    void accept(long orderId, long sequence, int submissionTick) {
        if (this.orderId != 0) {
            throw new IllegalStateException("Order has already been submitted");
        }
        this.orderId = orderId;
        this.sequence = sequence;
        this.submissionTick = submissionTick;
    }

    void recordFill(double quantity, double price) {
        if (quantity <= 0 || quantity > remainingQuantity + EPSILON) {
            throw new IllegalArgumentException("Invalid fill quantity");
        }
        remainingQuantity = Math.max(0, remainingQuantity - quantity);
        filledQuantity += quantity;
        executedNotional += quantity * price;
        status = remainingQuantity <= EPSILON ? OrderStatus.FILLED : OrderStatus.PARTIAL;
        statusReason = status == OrderStatus.FILLED
                ? "Order fully executed"
                : "Insufficient compatible liquidity for a full execution";
    }

    void reject(String reason) {
        finish(OrderStatus.REJECTED, reason);
    }

    void cancel(String reason) {
        finish(OrderStatus.CANCELLED, reason);
    }

    void expire() {
        finish(OrderStatus.EXPIRED, "Order expired before it could be fully executed");
    }

    private void finish(OrderStatus terminalStatus, String reason) {
        if (status == OrderStatus.FILLED) {
            return;
        }
        status = terminalStatus;
        statusReason = reason;
    }

    public long getOrderId() {
        return orderId;
    }

    public long getSequence() {
        return sequence;
    }

    public int getSubmissionTick() {
        return submissionTick;
    }

    public int getExpiryTick() {
        return expiryTick;
    }

    public Trader getTrader() {
        return trader;
    }

    public String getTraderId() {
        return trader.getName();
    }

    public Stock getStock() {
        return stock;
    }

    public String getStockSymbol() {
        return stock.getSymbol();
    }

    public OrderSide getSide() {
        return side;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public double getOriginalQuantity() {
        return originalQuantity;
    }

    public double getRemainingQuantity() {
        return remainingQuantity;
    }

    public double getFilledQuantity() {
        return filledQuantity;
    }

    public double getLimitPrice() {
        return limitPrice;
    }

    public double getAverageExecutionPrice() {
        return filledQuantity <= EPSILON ? 0 : executedNotional / filledQuantity;
    }

    public double getExecutedNotional() {
        return executedNotional;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public String getStatusReason() {
        return statusReason;
    }

    public boolean isBuy() {
        return side == OrderSide.BUY;
    }

    public boolean isOpen() {
        return status == OrderStatus.PENDING || status == OrderStatus.PARTIAL;
    }

    public boolean isExpiredAt(int tick) {
        return expiryTick >= 0 && tick >= expiryTick;
    }

    public double getReservedCash() {
        return reservedCash;
    }

    void setReservedCash(double reservedCash) {
        this.reservedCash = reservedCash;
    }

    void reduceReservedCash(double amount) {
        reservedCash = Math.max(0, reservedCash - amount);
    }

    public double getReservedShares() {
        return reservedShares;
    }

    void setReservedShares(double reservedShares) {
        this.reservedShares = reservedShares;
    }

    void reduceReservedShares(double amount) {
        reservedShares = Math.max(0, reservedShares - amount);
    }
}