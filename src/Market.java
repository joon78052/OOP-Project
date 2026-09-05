import java.util.ArrayList;

public class Market {

    private ArrayList<Stock> stocks;
    private ArrayList<Trader> traders;

    public Market() {
        stocks = new ArrayList<>();
        traders = new ArrayList<>();
    }

    public void update() {
        // Simulation logic will move prices later
    }

    public void updateButton() {
    }

    public void addStock(Stock stock) {
        stocks.add(stock);
    }

    public void addTrader(Trader trader) {
        traders.add(trader);
    }

    public ArrayList<Stock> getStocks() {
        return stocks;
    }
}
