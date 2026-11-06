public class Trade {

    private final long executionId;
    private final Stock stock;
    private final double price;
    private final double quantity;
    private final Trader buyer;
    private final Trader seller;
    private final long buyOrderId;
    private final long sellOrderId;
    private final OrderSide aggressorSide;
    private final int tick;

    public Trade(
            long executionId,
            Stock stock,
            double price,
            double quantity,
            Trader buyer,
            Trader seller,
            long buyOrderId,
            long sellOrderId,
            OrderSide aggressorSide,
            int tick
    ) {
        if (stock == null || buyer == null || seller == null || aggressorSide == null) {
            throw new IllegalArgumentException("Execution participants cannot be null");
        }
        if (price <= 0 || quantity <= 0
                || Double.isNaN(price) || Double.isNaN(quantity)
                || Double.isInfinite(price) || Double.isInfinite(quantity)) {
            throw new IllegalArgumentException("Execution price and quantity must be positive");
        }
        this.executionId = executionId;
        this.stock = stock;
        this.price = price;
        this.quantity = quantity;
        this.buyer = buyer;
        this.seller = seller;
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.aggressorSide = aggressorSide;
        this.tick = tick;
    }

    public long getExecutionId() {
        return executionId;
    }

    public Stock getStock() {
        return stock;
    }

    public double getPrice() {
        return price;
    }

    public double getQuantity() {
        return quantity;
    }

    public double getTotalUsd() {
        return price * quantity;
    }

    public double getMarketCap() {
        return price * Stock.SUPPLY;
    }

    public Trader getBuyer() {
        return buyer;
    }

    public Trader getSeller() {
        return seller;
    }

    public String getBuyerName() {
        return buyer.getName();
    }

    public String getSellerName() {
        return seller.getName();
    }

    public long getBuyOrderId() {
        return buyOrderId;
    }

    public long getSellOrderId() {
        return sellOrderId;
    }

    public OrderSide getAggressorSide() {
        return aggressorSide;
    }

    public boolean isBuy() {
        return aggressorSide == OrderSide.BUY;
    }

    public String getTraderTag() {
        return isBuy() ? getBuyerName() : getSellerName();
    }

    public int getTick() {
        return tick;
    }
}
