import java.awt.Color;
import java.awt.Graphics;

public class Candle {

    private double open;
    private double high;
    private double low;
    private double close;

    public Candle(double open, double high, double low, double close) {
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
    }

    public double highest() {
        return high;
    }

    public double lowest() {
        return low;
    }

    public void draw(Graphics g, int x, int width, int chartTop, int chartHeight,
                     double minPrice, double maxPrice) {
        int midX = x + width / 2;
        int yHigh = priceToY(high, chartTop, chartHeight, minPrice, maxPrice);
        int yLow = priceToY(low, chartTop, chartHeight, minPrice, maxPrice);
        int yOpen = priceToY(open, chartTop, chartHeight, minPrice, maxPrice);
        int yClose = priceToY(close, chartTop, chartHeight, minPrice, maxPrice);

        if (close >= open) {
            g.setColor(new Color(30, 140, 70));
        } else {
            g.setColor(new Color(180, 40, 40));
        }

        g.drawLine(midX, yHigh, midX, yLow);
        int bodyTop = Math.min(yOpen, yClose);
        int bodyHeight = Math.max(2, Math.abs(yClose - yOpen));
        g.fillRect(x + 1, bodyTop, Math.max(3, width - 2), bodyHeight);
    }

    private int priceToY(double price, int chartTop, int chartHeight,
                         double minPrice, double maxPrice) {
        double range = maxPrice - minPrice;
        if (range <= 0) {
            return chartTop + chartHeight / 2;
        }
        double fraction = (price - minPrice) / range;
        return chartTop + (int) ((1.0 - fraction) * chartHeight);
    }
}
