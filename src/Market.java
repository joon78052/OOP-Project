import java.util.ArrayList;

public class Market {

    private ArrayList<Stock> stocks;
    private ArrayList<Trader> traders;

    private Random random;
    private int tick;


    public Market() {
        stocks = new ArrayList<>();
        traders = new ArrayList<>();

        random = new Random();
        tick = 0;
    }

    public void update() {
        // Simulation logic will move prices later
        // This code will cause changes to the stock market in a logically random way

        tick++;

        for (Stock stock : stocks) {
            double movementPercent = (random.nextDouble() - 0.5) * 0.02;
            
            double newPrice = stock.getPrice() * (1 + movementPercent);
            
            stock.updatePrice(newPrice);
        }

    }

    // Removed updateButton method as the button will does not need market logic.


    public void addStock(Stock stock) {
        stocks.add(stock);
    }

    public void addTrader(Trader trader) {
        traders.add(trader);
    }

    public ArrayList<Stock> getStocks() {
        return stocks;
    }

    public int getTick() {
        return tick;
    }

}
