import java.util.ArrayList;

public class Stock {

    private static final int MAX_CANDLES = 60;


    public static final double SUPPLY = 1_000_000;

    private String symbol;
    private double price;
    private double openingPrice;
    private ArrayList<Candle> candles;

    public Stock(String symbol, double price) {
        this.symbol = symbol;
        this.price = price;
        this.openingPrice = price;
        this.candles = new ArrayList<>();
        candles.add(new Candle(price, price, price, price, 0));
    }

    public String getSymbol() {
        return symbol;
    }

    public double getPrice() {
        return price;
    }

    public double getMarketCap() {
        return price * SUPPLY;
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

    public void updatePrice(double newPrice, double volumeUsd) {

        double high =
                Math.max(price, newPrice);
    
        double low =
                Math.min(price, newPrice);
    
        updatePrice(
                newPrice,
                high,
                low,
                volumeUsd
        );
    }
    
    
    public void updatePrice(
            double newPrice,
            double high,
            double low,
            double volumeUsd
    ) {
    
        double open = price;
    
        high =
                Math.max(
                        high,
                        Math.max(open, newPrice)
                );
    
        low =
                Math.min(
                        low,
                        Math.min(open, newPrice)
                );
    
        low = Math.max(0.01, low);
    
        addCandle(
                open,
                high,
                low,
                newPrice,
                volumeUsd
        );
    
        this.price = newPrice;
    }

    private void addCandle(
        double open,
        double high,
        double low,
        double close,
        double volumeUsd
    ) {

        candles.add(
            new Candle(
                    open,
                    high,
                    low,
                    close,
                    volumeUsd
            )
        );

        if (candles.size() > MAX_CANDLES) {
            candles.remove(0);
        }
    }
}
