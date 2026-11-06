public class Player extends Trader {

    public Player(String name, double cash) {
        super(name, cash);
    }

    public void spend(double usd) throws OrderException {
        if (usd <= 0 || Double.isNaN(usd) || Double.isInfinite(usd)) {
            throw new OrderException("Amount must be positive and finite");
        }
        if (usd > getAvailableCash()) {
            throw new OrderException("Not enough cash");
        }
        cash -= usd;
    }

    public void receive(double usd) {
        receiveCash(usd);
    }
}
