public class Trade {

    public enum Type {
        BUY,
        SELL
    }

    private final Type type;
    private final Stock stock;
    private final double price;    
    private final double quantity;   
    private final String traderTag; 
    private final int tick;          

    public Trade(Type type, Stock stock, double price, double quantity,
                 String traderTag, int tick) {
        this.type = type;
        this.stock = stock;
        this.price = price;
        this.quantity = quantity;
        this.traderTag = traderTag;
        this.tick = tick;
    }

    public Type getType() {
        return type;
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

    public String getTraderTag() {
        return traderTag;
    }

    public int getTick() {
        return tick;
    }

    public boolean isBuy() {
        return type == Type.BUY;
    }
}
