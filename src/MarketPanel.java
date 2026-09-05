import javax.swing.JPanel;
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

        g.drawString("Stock Market Sim", 30, 30);
        g.drawString("Listings (will move later from traders)", 30, 55);

        ArrayList<Stock> stocks = market.getStocks();
        int y = 90;
        for (int i = 0; i < stocks.size(); i++) {
            Stock stock = stocks.get(i);
            String line = stock.getSymbol() + "   $" + String.format("%.2f", stock.getPrice());
            //formats the price to 2 decimal places
            g.drawString(line, 30, y);
            y += 25;
        }
    }
}
