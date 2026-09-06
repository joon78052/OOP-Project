import java.util.ArrayList;

public class Stock {

    private static final int MAX_CANDLES = 40;

    private String symbol;
    private double price;
    private double openingPrice;
    private ArrayList<Candle> candles;

    public Stock(String symbol, double price) {
        this.symbol = symbol;
        this.price = price;
        this.openingPrice = price;
        this.candles = new ArrayList<>();
        candles.add(new Candle(price, price, price, price));
    }

    public String getSymbol() {
        return symbol;
    }

    public double getPrice() {
        return price;
    }

    public ArrayList<Candle> getCandles() {
        return candles;
    }

    public void updatePrice(double newPrice) {
        addCandle(price, newPrice);
        this.price = newPrice;
    }

    private void addCandle(double open, double close) {
        double wick = Math.abs(close - open) * 0.35 + price * 0.004;
        double high = Math.max(open, close) + wick;
        double low = Math.max(0.01, Math.min(open, close) - wick);
        candles.add(new Candle(open, high, low, close));
        if (candles.size() > MAX_CANDLES) {
            candles.remove(0);
        }
    }
}
