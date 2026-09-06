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

    public double getOpeningPrice() {
        return openingPrice;
    }

    public double getChange() {
        return price - openingPrice;
    }

    public double getChangePercent() {
        if (openingPrice == 0) {
            return 0;
        }
        return (getChange() / openingPrice) * 100.0;
    }

    public double getDayLow() {
        double low = price;
        for (int i = 0; i < candles.size(); i++) {
            low = Math.min(low, candles.get(i).lowest());
        }
        return low;
    }

    public double getDayHigh() {
        double high = price;
        for (int i = 0; i < candles.size(); i++) {
            high = Math.max(high, candles.get(i).highest());
        }
        return high;
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
