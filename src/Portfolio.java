import java.util.HashMap;
import java.util.Map;

public class Portfolio {

    private static final double EPSILON = 0.0000001;

    private final Map<Stock, Double> holdings;
    private final Map<Stock, Double> reserved;

    public Portfolio() {
        holdings = new HashMap<>();
        reserved = new HashMap<>();
    }

    public double getShares(Stock stock) {
        return holdings.getOrDefault(stock, 0.0);
    }

    public double getReservedShares(Stock stock) {
        return reserved.getOrDefault(stock, 0.0);
    }

    public double getAvailableShares(Stock stock) {
        return Math.max(0, getShares(stock) - getReservedShares(stock));
    }

    public void addShares(Stock stock, double quantity) {
        requirePositive(quantity, "Share quantity");
        holdings.put(stock, getShares(stock) + quantity);
    }

    public void removeShares(Stock stock, double quantity) throws OrderException {
        requirePositiveOrderValue(quantity, "Share quantity");
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

    public void reserveShares(Stock stock, double quantity) throws OrderException {
        requirePositiveOrderValue(quantity, "Share quantity");
        if (quantity > getAvailableShares(stock) + EPSILON) {
            throw new OrderException("Not enough available shares");
        }
        reserved.put(stock, getReservedShares(stock) + quantity);
    }

    public void releaseShares(Stock stock, double quantity) {
        if (quantity <= 0) {
            return;
        }
        double left = getReservedShares(stock) - quantity;
        if (left <= EPSILON) {
            reserved.remove(stock);
        } else {
            reserved.put(stock, left);
        }
    }

    public void settleReservedSale(Stock stock, double quantity) throws OrderException {
        if (quantity > getReservedShares(stock) + EPSILON) {
            throw new OrderException("Sale exceeds reserved shares");
        }
        releaseShares(stock, quantity);
        removeShares(stock, quantity);
    }

    private static void requirePositive(double value, String label) {
        if (value <= 0 || Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException(label + " must be positive and finite");
        }
    }

    private static void requirePositiveOrderValue(double value, String label)
            throws OrderException {
        if (value <= 0 || Double.isNaN(value) || Double.isInfinite(value)) {
            throw new OrderException(label + " must be positive and finite");
        }
    }
}
