import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.ArrayList;
import javax.swing.JPanel;

public class MarketPanel extends JPanel {

    private static final Color BG = new Color(13, 16, 20);
    private static final Color CHART_BG = new Color(17, 21, 26);
    private static final Color GRID = new Color(38, 44, 52);
    private static final Color TEXT = new Color(210, 216, 224);
    private static final Color LABEL = new Color(125, 135, 148);
    private static final Color UP = Candle.UP;
    private static final Color DOWN = Candle.DOWN;

    private final Market market;
    private final Font titleFont = new Font("SansSerif", Font.BOLD, 15);
    private final Font priceFont = new Font("SansSerif", Font.BOLD, 26);
    private final Font changeFont = new Font("SansSerif", Font.BOLD, 13);
    private final Font bodyFont = new Font("SansSerif", Font.PLAIN, 12);
    private final Font smallFont = new Font("SansSerif", Font.PLAIN, 11);

    public MarketPanel(Market market) {
        this.market = market;
        setBackground(BG);
        setPreferredSize(new Dimension(900, 980));
    }

    @Override
    protected void paintComponent(Graphics Graph) {
        super.paintComponent(Graph);
        renderComponent(Graph);
    }

    private void renderComponent(Graphics Graph) {
        Graphics2D Graph2D = (Graphics2D) Graph;
        Graph2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        ArrayList<Stock> stocks = market.getStocks();
        int left = 24;
        int width = Math.max(400, getWidth() - 48);
        int y = 20;

        Graph2D.setFont(bodyFont);
        Graph2D.setColor(LABEL);
        Graph2D.drawString("Stock Market Sim", left, y);
        y += 14;

        int chartHeight = 210; 
        for (int i = 0; i < stocks.size(); i++) {
            Stock stock = stocks.get(i);
            y = drawQuoteHeader(Graph2D, stock, left, y, width);
            drawChart(Graph2D, stock, left, y, width, chartHeight);
            y += chartHeight + 16;
            y = drawStatsGrid(Graph2D, stock, left, y, width);
            y += 28;
        }
    }

    private int drawQuoteHeader(Graphics2D Graph, Stock stock, int x, int y, int width) {
        Graph.setColor(GRID);
        Graph.drawLine(x, y, x + width, y); // sep line
        y += 22;

        Graph.setFont(titleFont);
        Graph.setColor(TEXT);
        Graph.drawString(stock.getSymbol() + "/USD", x, y);

        ArrayList<Candle> candles = stock.getCandles();
        Candle last = candles.get(candles.size() - 1);
        Color ohlcColor = last.isUp() ? UP : DOWN;
        String ohlc = "O " + money(stock.getOpeningPrice())
                + "  H " + money(stock.getDayHigh())
                + "  L " + money(stock.getDayLow())
                + "  C " + money(stock.getPrice());
        Graph.setColor(ohlcColor);
        int ohlcWidth = Graph.getFontMetrics().stringWidth(ohlc);
        Graph.drawString(ohlc, x + width - ohlcWidth, y);

        y += 32;

        Graph.setFont(priceFont);
        Graph.setColor(TEXT);
        Graph.drawString(money(stock.getPrice()), x, y);

        double change = stock.getChange();
        String changeText = formatChange(change) + " (" + formatChange(stock.getChangePercent()) + "%)";
        Graph.setFont(changeFont);
        Graph.setColor(change >= 0 ? UP : DOWN);
        Graph.drawString(changeText, x + 160, y);

        Graph.setFont(smallFont);
        Graph.setColor(LABEL);
        String mc = "MC " + compactUsd(stock.getMarketCap());
        int mcWidth = Graph.getFontMetrics().stringWidth(mc);
        Graph.drawString(mc, x + width - mcWidth, y);

        return y + 14;
    }

    private void drawChart(Graphics Graph, Stock stock, int left, int top, int width, int height) {
        int volumeHeight = 48;               
        int candleAreaHeight = height - volumeHeight - 6;

        Graph.setColor(CHART_BG);
        Graph.fillRect(left, top, width, height);
        Graph.setColor(GRID);
        Graph.drawRect(left, top, width, height);

        ArrayList<Candle> candles = stock.getCandles();
        if (candles.isEmpty()) {
            return;
        }

        double minPrice = stock.getDayLow();
        double maxPrice = stock.getDayHigh();
        double pad = Math.max(0.50, (maxPrice - minPrice) * 0.12);
        minPrice -= pad;
        maxPrice += pad;

        int gridLines = 4;
        Graph.setFont(smallFont);
        for (int i = 0; i <= gridLines; i++) {
            int lineY = top + 6 + (int) ((candleAreaHeight - 12) * (i / (double) gridLines));
            Graph.setColor(GRID);
            Graph.drawLine(left + 1, lineY, left + width - 1, lineY);

            double priceAtLine = maxPrice - (maxPrice - minPrice) * (i / (double) gridLines);
            Graph.setColor(LABEL);
            String label = money(priceAtLine);
            int labelWidth = Graph.getFontMetrics().stringWidth(label);
            Graph.drawString(label, left + width - labelWidth - 4, lineY - 3);
        }

        double maxVolume = 0;
        for (int i = 0; i < candles.size(); i++) {
            maxVolume = Math.max(maxVolume, candles.get(i).getVolume());
        }

        int candleWidth = Math.max(6, (width - 70) / Math.max(20, candles.size() + 2));
        int x = left + 8;
        int volumeTop = top + height - volumeHeight - 2;

        for (int i = 0; i < candles.size(); i++) {
            Candle candle = candles.get(i);
            candle.draw(Graph, x, candleWidth, top + 6, candleAreaHeight - 12, minPrice, maxPrice);
            candle.drawVolume(Graph, x, candleWidth, volumeTop, volumeHeight, maxVolume);
            x += candleWidth + 2;
        }

        Graphics2D Graph2D = (Graphics2D) Graph;
        Stroke old = Graph2D.getStroke();
        Graph2D.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                10, new float[]{3f, 4f}, 0));
        int priceY = top + 6 + (int) ((1.0 - (stock.getPrice() - minPrice) / (maxPrice - minPrice))
                * (candleAreaHeight - 12));
        Candle last = candles.get(candles.size() - 1);
        Graph2D.setColor(last.isUp() ? UP : DOWN);
        Graph2D.drawLine(left + 1, priceY, left + width - 1, priceY);
        Graph2D.setStroke(old);
    }

    private int drawStatsGrid(Graphics2D Graph, Stock stock, int x, int y, int width) {
        int colW = width / 4;
        int rowH = 24;

        String[][] cells = {
            {"Open", money(stock.getOpeningPrice()),
                "Day range", money(stock.getDayLow()) + " - " + money(stock.getDayHigh()),
                "Market cap", compactUsd(stock.getMarketCap()),
                "Supply", compact(Stock.SUPPLY)},
            {"Last", money(stock.getPrice()),
                "Change", formatChange(stock.getChange()) + " (" + formatChange(stock.getChangePercent()) + "%)",
                "Sellers", "--",
                "Cash idle", "--"},
            {"Previous close", money(stock.getOpeningPrice()),
                "Volume", compactUsd(totalVolume(stock)),
                "Retail flow", "--",
                "Traders", "--"},
            {"Last trade", "--",
                "Institutional flow", "--",
                "Status", "Waiting for traders",
                "", ""}
        };

        Stroke old = Graph.getStroke();
        Graph.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                10, new float[]{2f, 3f}, 0));

        for (int row = 0; row < cells.length; row++) {
            int rowY = y + row * rowH;
            Graph.setColor(GRID);
            Graph.drawLine(x, rowY, x + width, rowY);

            for (int col = 0; col < 4; col++) {
                int cellX = x + col * colW;
                String label = cells[row][col * 2];
                String value = cells[row][col * 2 + 1];
                Graph.setFont(smallFont);
                Graph.setColor(LABEL);
                Graph.drawString(label, cellX + 4, rowY + 16);
                Graph.setFont(bodyFont);
                Graph.setColor(TEXT);
                int valueWidth = Graph.getFontMetrics().stringWidth(value);
                Graph.drawString(value, cellX + colW - valueWidth - 12, rowY + 16);
            }
        }

        int bottom = y + cells.length * rowH;
        Graph.setColor(GRID);
        Graph.drawLine(x, bottom, x + width, bottom);
        Graph.setStroke(old);

        return bottom + 20;
    }

    private double totalVolume(Stock stock) {
        double total = 0;
        ArrayList<Candle> candles = stock.getCandles();
        for (int i = 0; i < candles.size(); i++) {
            total += candles.get(i).getVolume();
        }
        return total;
    }

    private String money(double value) {
        return "$" + String.format("%.2f", value);
    }

    private String compactUsd(double value) {
        return "$" + compact(value);
    }

    private String compact(double value) {
        if (value >= 1_000_000_000) {
            return String.format("%.2fB", value / 1_000_000_000);
        }
        if (value >= 1_000_000) {
            return String.format("%.2fM", value / 1_000_000);
        }
        if (value >= 1_000) {
            return String.format("%.1fK", value / 1_000);
        }
        return String.format("%.2f", value);
    }

    private String formatChange(double value) {
        String sign = value > 0 ? "+" : "";
        return sign + String.format("%.2f", value);
    }
}
