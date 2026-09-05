public class Order {

    private Trader trader;
    private Stock stock;
    private int quantity;
    private String type;

    public Order(Trader trader, Stock stock, int quantity, String type) {
        this.trader = trader;
        this.stock = stock;
        this.quantity = quantity;
        this.type = type;
    }
}
