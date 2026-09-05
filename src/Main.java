public class Main {
    public static void main(String[] args) {
        Market market = new Market();

        javax.swing.JFrame frame =
                new javax.swing.JFrame("Stock Market Simulator");

        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.add(new MarketPanel(market));
        frame.setVisible(true);
    }
}
