import java.awt.Color;
import java.awt.Graphics;

public class Candle {

    public static final Color UP = new Color(34, 197, 94);
    public static final Color DOWN = new Color(239, 68, 68);

    private double open;
    private double high;
    private double low;
    private double close;
    private double volume; 

    public Candle(double open, double high, double low, double close, double volume) {
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }

    public double highest() {
        return high;
    }

    public double lowest() {
        return low;
    }

    public double getVolume() {
        return volume;
    }

    public boolean isUp() {
        return close >= open;
    }

    public void draw(Graphics g, int x, int width, int chartTop, int chartHeight,
                     double minPrice, double maxPrice) {
        int midX = x + width / 2;
        int yHigh = priceToY(high, chartTop, chartHeight, minPrice, maxPrice);
        int yLow = priceToY(low, chartTop, chartHeight, minPrice, maxPrice);
        int yOpen = priceToY(open, chartTop, chartHeight, minPrice, maxPrice);
        int yClose = priceToY(close, chartTop, chartHeight, minPrice, maxPrice);

        g.setColor(isUp() ? UP : DOWN);

        g.drawLine(midX, yHigh, midX, yLow);
        int bodyTop = Math.min(yOpen, yClose);
        int bodyHeight = Math.max(2, Math.abs(yClose - yOpen));
        g.fillRect(x + 1, bodyTop, Math.max(3, width - 2), bodyHeight);
    }

    public void drawVolume(Graphics g, int x, int width, int areaTop, int areaHeight,
                           double maxVolume) {
        if (maxVolume <= 0) {
            return;
        }
        int barHeight = (int) ((volume / maxVolume) * (areaHeight - 2));
        barHeight = Math.max(1, barHeight);

        Color base = isUp() ? UP : DOWN;
        g.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), 110));
        g.fillRect(x + 1, areaTop + areaHeight - barHeight, Math.max(3, width - 2), barHeight);
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
