import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class MarketPanel extends JPanel {

    private final Market market;

    public MarketPanel(Market market) {
        this.market = market;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        renderComponent(g);
    }
    
    private void renderComponent(Graphics g) {
        g.setColor(Color.BLACK);
        g.drawString("Stock Market Sim", 30, 24);
        g.drawString("Initial prices (will move later from traders)", 30, 42);

        ArrayList<Stock> stocks = market.getStocks();
        int y = 60;
        int chartHeight = 180;
        int chartLeft = 30;
        int chartWidth = getWidth() - 60;

        for (int i = 0; i < stocks.size(); i++) {
            Stock stock = stocks.get(i);
            g.setColor(Color.BLACK);
            g.drawString("$" + stock.getSymbol() + "   $" + String.format("%.2f", stock.getPrice()),
                    chartLeft, y);

            drawChart(g, stock, chartLeft, y + 10, chartWidth, chartHeight);
            y += chartHeight + 50;
        }
    }

    private void drawChart(Graphics g, Stock stock, int left, int top, int width, int height) {
        g.setColor(new Color(230, 230, 230));
        g.fillRect(left, top, width, height);
        g.setColor(Color.GRAY);
        g.drawRect(left, top, width, height);

        ArrayList<Candle> candles = stock.getCandles();
        if (candles.isEmpty()) {
            return;
        }

        //find the high low prices


        double minPrice = candles.get(0).lowest();
        double maxPrice = candles.get(0).highest();
        for (int i = 0; i < candles.size(); i++) {
            Candle candle = candles.get(i);
            minPrice = Math.min(minPrice, candle.lowest());
            maxPrice = Math.max(maxPrice, candle.highest());
        }
        double pad = Math.max(0.50, (maxPrice - minPrice) * 0.12);
        minPrice -= pad;
        maxPrice += pad;

        int candleWidth = Math.max(6, width / Math.max(20, candles.size() + 2));
        int x = left + 8;
        for (int i = 0; i < candles.size(); i++) {
            candles.get(i).draw(g, x, candleWidth, top + 4, height - 8, minPrice, maxPrice);
            x += candleWidth + 2;
        }
    }
}
