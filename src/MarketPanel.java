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

    private static final Color UP = new Color(30, 140, 70);
    private static final Color DOWN = new Color(180, 40, 40);
    private static final Color LABEL = new Color(90, 90, 90);
    private static final Color LINE = new Color(190, 190, 190);

    private final Market market;
    private final Font titleFont = new Font("SansSerif", Font.BOLD, 16);
    private final Font priceFont = new Font("SansSerif", Font.BOLD, 28);
    private final Font changeFont = new Font("SansSerif", Font.BOLD, 14);
    private final Font bodyFont = new Font("SansSerif", Font.PLAIN, 12);
    private final Font smallFont = new Font("SansSerif", Font.PLAIN, 11);

    public MarketPanel(Market market) {
        this.market = market;
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(980, 920));
    }

    @Override
    protected void paintComponent(Graphics Graph) {
        super.paintComponent(Graph);
        renderComponent(Graph);
    }

    private void renderComponent(Graphics Graph) {
        Graphics2D Graph2D = (Graphics2D) Graph;
        Graph2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        ArrayList<Stock> stocks = market.getStocks(); //Stock list from th market
        int left = 24;
        int width = Math.max(400, getWidth() - 48);
        int y = 20;

        Graph2D.setFont(bodyFont);
        Graph2D.setColor(LABEL);
        Graph2D.drawString("Stock Market Sim", left, y);
        y += 18;

        int chartHeight = 150;
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
        Graph.setColor(LINE);
        Graph.drawLine(x, y, x + width, y); // sep line
        y += 22;

        Graph.setFont(titleFont);
        Graph.setColor(Color.BLACK);
        Graph.drawString("$" + stock.getSymbol(), x, y);
        y += 32;

        Graph.setFont(priceFont);
        Graph.drawString(money(stock.getPrice()), x, y);

        double change = stock.getChange();
        String changeText = formatChange(change) + " (" + formatChange(stock.getChangePercent()) + "%)";
        Graph.setFont(changeFont);
        Graph.setColor(change >= 0 ? UP : DOWN);
        Graph.drawString(changeText, x + 150, y);

        y += 20;
        Graph.setFont(smallFont);
        Graph.setColor(LABEL);
        Graph.drawString("Open " + money(stock.getOpeningPrice())
                + "   Day range " + money(stock.getDayLow())
                + " – " + money(stock.getDayHigh()), x, y);
        return y + 12;
    }

    private int drawStatsGrid(Graphics2D Graph, Stock stock, int x, int y, int width) {
        int colW = width / 4;
        int rowH = 24;

        String[][] cells = {
            {"Open", money(stock.getOpeningPrice()),
                "Day range", money(stock.getDayLow()) + " – " + money(stock.getDayHigh()),
                "Buyers", "--",
                "Held", "--"},
            {"Last", money(stock.getPrice()),
                "Change", formatChange(stock.getChange()) + " (" + formatChange(stock.getChangePercent()) + "%)",
                "Sellers", "--",
                "Cash idle", "--"},
            {"Previous close", money(stock.getOpeningPrice()),
                "Volume", "--",
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
            Graph.setColor(LINE);
            Graph.drawLine(x, rowY, x + width, rowY);

            for (int col = 0; col < 4; col++) {
                int cellX = x + col * colW;
                String label = cells[row][col * 2];
                String value = cells[row][col * 2 + 1];
                Graph.setFont(smallFont);
                Graph.setColor(LABEL);
                Graph.drawString(label, cellX + 4, rowY + 16);
                Graph.setFont(bodyFont);
                Graph.setColor(Color.BLACK);
                int valueWidth = Graph.getFontMetrics().stringWidth(value);
                Graph.drawString(value, cellX + colW - valueWidth - 12, rowY + 16);
            }
        }

        int bottom = y + cells.length * rowH;
        Graph.setColor(LINE);
        Graph.drawLine(x, bottom, x + width, bottom);
        Graph.setStroke(old);

        Graph.setFont(smallFont);
        Graph.setColor(LABEL); //Change this later to 


        return bottom + 20;
    }

    private void drawChart(Graphics Graph, Stock stock, int left, int top, int width, int height) {
        Graph.setColor(new Color(245, 245, 245));
        Graph.fillRect(left, top, width, height);
        Graph.setColor(Color.GRAY);
        Graph.drawRect(left, top, width, height);

        ArrayList<Candle> candles = stock.getCandles();
        if (candles.isEmpty()) {
            return;
        }

        double minPrice = stock.getDayLow();
        double maxPrice = stock.getDayHigh();
        double pad = Math.max(0.50, (maxPrice - minPrice) * 0.12); // padding to the min and max price.
        minPrice -= pad;
        maxPrice += pad;

        int candleWidth = Math.max(6, width / Math.max(20, candles.size() + 2));
        int x = left + 8;
        for (int i = 0; i < candles.size(); i++) {
            candles.get(i).draw(Graph, x, candleWidth, top + 4, height - 8, minPrice, maxPrice);
            x += candleWidth + 2;
        }
    }

    private String money(double value) {
        return "$" + String.format("%.2f", value);
    }

    private String formatChange(double value) {
        String sign = value > 0 ? "+" : "";
        return sign + String.format("%.2f", value);
    }
}
