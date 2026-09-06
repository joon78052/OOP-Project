import javax.swing.JFrame;
import javax.swing.JScrollPane;

public class Main {
    public static void main(String[] args) {
        Market market = new Market();
        market.addStock(new Stock("JOON", 100.00));
        market.addStock(new Stock("HXMZA", 75.00));

        JFrame frame = new JFrame("Stock Market Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1020, 740);
        frame.add(new JScrollPane(new MarketPanel(market)));
        frame.setVisible(true);
    }
}
