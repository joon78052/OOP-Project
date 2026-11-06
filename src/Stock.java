import java.util.ArrayList;

public class Stock {

    private static final int MAX_CANDLES = 60;


    public static final double SUPPLY = 1_000_000;

    private String symbol;
    private double price;
    private double openingPrice;
    private ArrayList<Candle> candles;
    private double tickOpen;
    private double tickHigh;
    private double tickLow;
    private double tickVolumeUsd;

    public Stock(String symbol, double price) {
        this.symbol = symbol;
        this.price = price;
        this.openingPrice = price;
        this.candles = new ArrayList<>();
        this.tickOpen = price;
        this.tickHigh = price;
        this.tickLow = price;
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

    public void recordExecution(double executionPrice, double quantity) {
        if (executionPrice <= 0 || quantity <= 0
                || Double.isNaN(executionPrice) || Double.isNaN(quantity)
                || Double.isInfinite(executionPrice) || Double.isInfinite(quantity)) {
            throw new IllegalArgumentException("Execution values must be positive and finite");
        }
        price = executionPrice;
        tickHigh = Math.max(tickHigh, executionPrice);
        tickLow = Math.min(tickLow, executionPrice);
        tickVolumeUsd += executionPrice * quantity;
    }

    public void closeTick() {
        addCandle(tickOpen, tickHigh, tickLow, price, tickVolumeUsd);
        tickOpen = price;
        tickHigh = price;
        tickLow = price;
        tickVolumeUsd = 0;
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
