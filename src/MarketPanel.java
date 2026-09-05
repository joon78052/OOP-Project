import javax.swing.JPanel;
import java.awt.Graphics;

public class MarketPanel extends JPanel {

    private final Market market;

    public MarketPanel(Market market) {
        this.market = market;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawString("Stock Market Sim", 30, 30);
        g.drawString("The simulation will appear here.", 30, 60);
    }
}
