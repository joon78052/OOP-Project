public class Main {
    public static void main(String[] args) {
        Market market = new Market();
        market.addStock(new Stock("JOON", 100.00));
        market.addStock(new Stock("HXMZA", 75.00));

        javax.swing.JFrame frame =
                new javax.swing.JFrame("Stock Market Simulator");

        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.add(new MarketPanel(market));
        frame.setVisible(true);
    }
}
