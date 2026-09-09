import java.util.HashMap;

public class Portfolio {

    private HashMap<Stock, Double> holdings;

    public Portfolio() {
        holdings = new HashMap<>();
    }

    public double getShares(Stock stock) {
        Double shares = holdings.get(stock);
        return shares == null ? 0 : shares;
    }

    public void addShares(Stock stock, double quantity) {
        holdings.put(stock, getShares(stock) + quantity);
    }

    public void removeShares(Stock stock, double quantity) throws OrderException {
        double current = getShares(stock);
        if (quantity > current + 0.0000001) {
            throw new OrderException("Not enough shares to sell");
        }
        double left = current - quantity;
        if (left < 0.0000001) {
            holdings.remove(stock);
        } else {
            holdings.put(stock, left);
        }
    }
}
